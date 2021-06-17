'use strict';

import {TOKEN} from "../Map/ResultMap";
import {getRandomInt, normalRandomScaled, randomDate} from "./RandomNumberGenerator";
import Circle from '@turf/circle';
import {randomPoint} from '@turf/random';
import inside from '@turf/inside';

var randomNormal = require('random-normal');

const extent = require('turf-extent');

const electron = window.require('electron');

const ipcRenderer = electron.ipcRenderer;

const Store = window.require('electron-store');
const store = new Store();

/**
 * Takes a number and a feature and {@link Polygon} or {@link MultiPolygon} and returns {@link Points} that reside inside the polygon. The polygon can
 * be convex or concave. The function accounts for holes.
 *
 * * Given a {Number}, the number of points to be randomly generated.
 * * Given a {@link Polygon} or {@link MultiPolygon}, the boundary of the random points
 *
 *
 * @module turf-random-points-on-polygon
 * @category measurement
 * @param {Number} number of points to be generated
 * @param {Feature<(Polygon|MultiPolygon)>} polygon input polygon or multipolygon
 * @param {Object} [properties={}] properties to be appended to the point features
 * @param {Boolean} [fc=false] if true returns points as a {@link FeatureCollection}
 * @param {Canvas} canvas
 * @return {Array} || {FeatureCollection<Points>} an array or feature collection of the random points inside the polygon
 **/


function getMapSvg(number, polygon, properties, fc, box, details, callback) {

    let requestOptions = {
        method: 'GET',
        redirect: 'follow'
    };
    fetch(`https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/[${box[0]},${box[1]},${box[2]},${box[3]}]/800x800@2x?access_token=${TOKEN}`, requestOptions)
        .then(response => response.blob())
        .then(blob => {
            let img = new Image;


            let canvas = document.createElement('canvas');
            img.onload = async function () {
                canvas.width = img.width;
                canvas.height = img.height;

                canvas.getContext('2d').drawImage(img, 0, 0, img.width, img.height);
                try{
                    const data = await process(number, polygon, properties, fc, canvas, box, details);
                    callback(data);
                }catch (e){
                    alert("An error happened during data simulation : e= " + e);
                    callback([]);
                }
            };
            img.src = URL.createObjectURL(blob);


        })
        .catch(error => console.log('error', error));
}

const randomPointsOnPolygon = (number, polygon, properties, fc, bbox, details, callback) => {
    getMapSvg(number, polygon, properties, fc, bbox, details, callback);
}

async function process(number, polygon, properties, fc, canvas, boundingBox, details) {
    const result = [];

    if (typeof properties === 'boolean') {
        fc = properties;
        properties = {};
    }

    if (number < 1) {
        return new Error('Number must be >= 1');
    }

    if (polygon.type !== 'Feature') {
        return new Error('Polygon parameter must be a Feature<(Polygon|MultiPolygon)>');

        if (polygon.geomtry.type !== 'Polygon' || polygon.geomtry.type !== 'MutliPolygon') {
            return new Error('Polygon parameter must be a Feature<(Polygon|MultiPolygon)>')
        }
    }

    if (this instanceof randomPointsOnPolygon) {
        return new randomPointsOnPolygon(number, polygon, properties);
    }

    setBBoxData(boundingBox);

    properties = properties || {};
    fc = fc || false;
    var points = [];
    var bbox = extent(polygon);
    var count = Math.round(parseFloat(number));

    const ctx = canvas.getContext('2d');
    let numberOfAttempts = 0;

    for (let i = 0; i < count; i++) {
        numberOfAttempts++;

        let point = randomPoint(1, {bbox: bbox});

        if (inside(point.features[0], polygon) === false) {
            i = --i;
        }


        if (inside(point.features[0], polygon) === true) {
            const p = JSON.parse(JSON.stringify(point));
            p.features[0].properties = JSON.parse(JSON.stringify(properties));

            if (isOnWater(p.features[0].geometry.coordinates[1], p.features[0].geometry.coordinates[0], ctx)) {
                i = --i;
            } else {
                points.push(p.features[0]);
            }
        }

        if (numberOfAttempts > count * count) {
            console.log("stop simulating points after " + numberOfAttempts + " attempts");
            return [];
        }

    }

    const api_key = store.get("googleApiKey");

    for (let tempPoint of points) {

        let randomDistance = -1;
        let noa = 1000;
        while (randomDistance < 0){
            randomDistance = randomNormal({mean:parseFloat(details.averageTravelDistance), dev:parseFloat(details.travelDistanceStandardDeviation)});
            noa--;
            if(noa<0){
                throw "Could not generate the random distance. Please check the mean and std value."
            }
        }
        console.log(randomDistance);
        const options = {steps: 50, units: 'kilometers'};
        let poly = Circle(tempPoint.geometry.coordinates, randomDistance, options);
        const coordinates = poly.geometry.coordinates[0];


        while (coordinates.length > 0) {
            const index = getRandomInt(coordinates.length);
            let pt1 = {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "Point",
                    "coordinates": coordinates[index]
                }
            };
            if (inside(pt1, polygon) === true && !isOnWater(coordinates[index][1], coordinates[index][0], ctx)) {

                const randomDepartureTime = randomDate(details.startTime, details.endTime);
                const dataItem = {
                    origin: tempPoint,
                    destination: pt1,
                    departureTime: randomDepartureTime
                };
                let randomDepartureTime_utc = Date.UTC(randomDepartureTime.getUTCFullYear(), randomDepartureTime.getUTCMonth(), randomDepartureTime.getUTCDate(),
                    randomDepartureTime.getUTCHours(), randomDepartureTime.getUTCMinutes(), randomDepartureTime.getUTCSeconds());

                const direction = await ipcRenderer.invoke('google_direction', {
                    origin: tempPoint.geometry.coordinates, destination: pt1.geometry.coordinates,
                    departureTime: Math.round(randomDepartureTime_utc / 1000),
                    travelMode: details.travelMode,
                    api_key: api_key
                });
                dataItem.direction = JSON.parse(direction);
                result.push(dataItem);
                break;
            } else {
                coordinates.splice(index, 1);
            }
        }

    }

    return result;

}

let bboxData = {};


const width = 1600,
    height = 1600;


const isOnWater = (lat, lon, ctx) => {
    const pixels = mapProject(lat, lon);

    let data = ctx.getImageData(pixels.x, pixels.y, 1, 1).data;

    if (data[2] > 200 && data[2] > data[1] && data[2] > data[0]) {
        return true;
    }
    return false;

}

const setBBoxData = (bbox) => {
    const south = deg2rad(bbox[1]),
        north = deg2rad(bbox[3]),
        west = deg2rad(bbox[0]),
        east = deg2rad(bbox[2]);

    let ymin = mercY(south),
        ymax = mercY(north),
        xFactor = width / (east - west),
        yFactor = height / (ymax - ymin);
    bboxData = {
        south,
        north,
        west,
        east,
        ymin,
        ymax,
        xFactor,
        yFactor
    }
}


function deg2rad(degrees) {
    return degrees * (Math.PI / 180.0);
}

function mercY($lat) {
    return Math.log(Math.tan($lat / 2 + Math.PI / 4));
}

function mapProject(lat, lon) {
    lat = deg2rad(lat);
    lon = deg2rad(lon);
    let x = lon,
        y = mercY(lat);

    x = (x - bboxData.west) * bboxData.xFactor;
    y = (bboxData.ymax - y) * bboxData.yFactor; // y points south
    return {x, y};
}

export default randomPointsOnPolygon;