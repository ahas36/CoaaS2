import React, {useState, useEffect} from 'react';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import clsx from 'clsx';
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import {FormControl, FormHelperText, FormLabel, Grid, Switch, Typography} from "@material-ui/core";
import Stepper from '@material-ui/core/Stepper';
import InputAdornment from '@material-ui/core/InputAdornment';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import MapGL, {Popup, NavigationControl, FullscreenControl, ScaleControl, CanvasOverlay} from 'react-map-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import MAP_STYLE from '../Map/map-style';
import {TOKEN} from '../Map/ResultMap';
import Pins from "./DSPin";
import LineChunk from '@turf/line-chunk';
import {lineString} from '@turf/helpers';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Backdrop from '@material-ui/core/Backdrop';
import CircularProgress from '@material-ui/core/CircularProgress';
import {
    MuiPickersUtilsProvider,
    KeyboardTimePicker,
    KeyboardDatePicker,
} from '@material-ui/pickers';
import 'date-fns';
import DateFnsUtils from '@date-io/date-fns';
import Circle from '@turf/circle';
import Bbox from '@turf/bbox';
import randomPointsOnPolygon from './RandomPointsGenerator';
import JSONTree from "react-json-tree";
import {getRandomInt} from "./RandomNumberGenerator";

let moment = require('moment');

const polylineDecoder = require('google-polyline');
const Store = window.require('electron-store');
const store = new Store();

const electron = window.require('electron');

const {dialog} = electron.remote;

const fs = electron.remote.require('fs');


Date.prototype.addHours = function (h) {
    this.setTime(this.getTime() + (h * 60 * 60 * 1000));
    return this;
}

Date.prototype.addSeconds = function (s) {
    this.setTime(this.getTime() + (s * 1000));
    return this;
}


function saveFile(content, exnt) {

    dialog.showSaveDialog({}).then((result) => {
        let fileName = result.filePath;
        if (fileName === undefined) {
            return;
        }

        // fileName is a string that contains the path and filename created in the save file dialog.
        if (!fileName.endsWith(exnt)) {
            fileName = fileName + '.' + exnt;
        }
        fs.writeFile(fileName, content, (err) => {
            if (err) {
                alert("An error occurred creating the file");
            }
        });
    });
}


const useStyles = makeStyles(theme => ({
    root: {
        width: '100%',
        height: '100%'
    },
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    button: {
        marginRight: theme.spacing(1),
    },
    instructions: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(1),
    },
    form: {
        margin: theme.spacing(2)
    },
    stepContentContainer: {}


}));

const fullscreenControlStyle = {
    position: 'absolute',
    top: 0,
    left: 0,
    padding: '10px'
};

const navStyle = {
    position: 'absolute',
    top: 36,
    left: 0,
    padding: '10px'
};

const scaleControlStyle = {
    position: 'absolute',
    bottom: 36,
    left: 0,
    padding: '10px'
};


function getSteps() {
    return ['Google API Key', 'Area', 'Details', 'Result'];
}

function isNumeric(str) {
    if (typeof str != "string") return false // we only process strings!
    return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
        !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
}

export default function AffiliationDashboard({updateAffiliationRequest, ...props}) {
    const classes = useStyles();
    const theme = useTheme();
    const steps = getSteps();

    const [isLoading, setIsLoading] = React.useState(false);
    const [journeyPointsGenerationProgress, setJourneyPointsGenerationProgress] = React.useState('0');
    const [activeStep, setActiveStep] = React.useState(0);

    const [simulatedData, setSimulatedData] = React.useState({});

    const [area, setArea] = useState({
        latitude: -37.817442,
        longitude: 144.968365,
        r: 25
    });

    const [viewport, setViewport] = useState({
        latitude: -37.817442,
        longitude: 144.968365,
        zoom: 9,
        bearing: 0,
        pitch: 0
    });

    const [googleApiKey, setGoogleApiKey] = React.useState(store.get("googleApiKey", null));

    const todayDate = new Date();

    let todayDateNextHour = new Date();

    todayDateNextHour = todayDateNextHour.addHours(2)

    const [details, setDetails] = useState({
        numberOfInstances: 10,
        averageTravelDistance: 4.1,
        travelDistanceStandardDeviation: 1.3,
        isTwoWay: true,
        timeInterval: 1,
        travelMode: "bicycling",
        date: todayDate,
        startTime: todayDate,
        endTime: todayDateNextHour,
    });

    const travelModeOptions = ["bicycling", "driving", "walking", "transit"]

    const [journeyPoints, setJourneyPoints] = useState([]);


    const handleDetailsChanged = (name) => (e) => {
        let newValue = details[name];
        switch (name) {
            case "startTime":
            case "endTime":
            case "date":
                newValue = e;
                break;
            case 'isTwoWay':
                newValue = !details.isTwoWay;
            default:
                newValue = e.target.value;
        }
        if (name == 'date') {
            const timeStart = {
                h: details.startTime.getHours(),
                m: details.startTime.getMinutes(),
                s: details.startTime.getSeconds(),
            };
            const timeEnd = {
                h: details.endTime.getHours(),
                m: details.endTime.getMinutes(),
                s: details.endTime.getSeconds(),
            };

            const startDate = new Date(newValue.getTime());
            startDate.setHours(timeStart.h);
            startDate.setMinutes(timeStart.m);
            startDate.setSeconds(timeStart.s);
            const endDate = new Date(newValue.getTime());
            endDate.setHours(timeEnd.h);
            endDate.setMinutes(timeEnd.m);
            endDate.setSeconds(timeEnd.s);
            setDetails({
                ...details,
                startTime: startDate,
                endTime: endDate,
                date: newValue
            });
        } else {
            setDetails({...details, [name]: newValue});
        }

    }


    const updateArea = (type) => (e) => {
        setArea({
            ...area,
            [type]: e.target.value
        });
        try {
            if (isNumeric(e.target.value)) {
                const v = Number(e.target.value);
                setViewport({
                    ...viewport,
                    [type]: v,
                })
            }
        } catch (err) {
        }

    }

    const onMapClick = (event) => {
        setArea({
            ...area,
            latitude: event.lngLat[1],
            longitude: event.lngLat[0]
        });
    }


    const drawArea = ({ctx, project, ...props}) => {
        try {
            const center = [area.longitude, area.latitude];
            const radius = area.r;
            const options = {steps: 50, units: 'kilometers'};
            const poly = Circle(center, radius, options);
            setArea({...area, poly, bbox: Bbox(poly)});
            const coordinates = poly.geometry.coordinates[0];

            ctx.clearRect(0, 0, props.width, props.height);

            let xy = project(center);
            ctx.fillStyle = "rgba(0, 0, 0, 1)";
            ctx.fillRect(xy[0], xy[1], 2, 2);

            ctx.beginPath();
            xy = project(coordinates[0]);
            ctx.moveTo(xy[0], xy[1]);
            for (let i = 1; i < coordinates.length; i++) {
                xy = project(coordinates[i]);
                ctx.lineTo(xy[0], xy[1])
            }

            ctx.stroke();
            ctx.fillStyle = "rgba(242, 234, 171, 0.53)";

            ctx.fill();


        } catch (e) {

        }

    }

    const drawPath = ({ctx, project, ...props}) => {
        ctx.clearRect(0, 0, props.width, props.height);
        for (let journey of journeyPoints) {
            const points = polylineDecoder.decode(journey.direction.routes[0].overview_polyline.points);
            ctx.beginPath();
            let startPoint = project([points[0][1], points[0][0]]);
            ctx.moveTo(startPoint[0], startPoint[1]);
            for (let i = 1; i < points.length; i++) {
                const nextPoint = project([points[i][1], points[i][0]]);
                ctx.lineTo(nextPoint[0], nextPoint[1])
            }
            ctx.stroke();
        }
    }
    const onMapError = (e) => {

        console.log(e);
    }

    const getStepContent = (step) => {
        switch (step) {
            case 0:
                return (
                    <div className={classes.form}>
                        Enter Your Google Map API key:
                        <Typography variant='subtitle2'>
                            The <a
                            href="https://console.cloud.google.com/apis/library/directions-backend.googleapis.com">
                            direction API
                        </a>
                            should be enabled.
                            <TextField
                                autoFocus
                                margin="dense"
                                value={googleApiKey}
                                onChange={(e) => {
                                    setGoogleApiKey(e.target.value);
                                    store.set("googleApiKey", e.target.value);
                                }}
                                label="Google API Key"
                                fullWidth
                            />
                        </Typography>
                    </div>
                );
            case 1:
                return (
                    <div className={classes.stepContentContainer}>
                        <div className={classes.form}>
                            <FormControl component="fieldset" className={classes.formControl} fullWidth>
                                <FormLabel component="legend">Center Location</FormLabel>
                                <Grid container spacing={3} style={{width: '100%'}}>
                                    <Grid item xs={4} s={4}>
                                        <TextField label='Latitude' value={area.latitude}
                                                   fullWidth
                                                   type="number"
                                                   onChange={updateArea('latitude')}/>
                                    </Grid>
                                    <Grid item xs={4} s={4}>
                                        <TextField label='Longitude' value={area.longitude}
                                                   fullWidth
                                                   type="number"
                                                   onChange={updateArea('longitude')}/>
                                    </Grid>
                                    <Grid item xs={4} s={4}>
                                        <TextField
                                            label="Radius"
                                            id="radius-tf"
                                            fullWidth
                                            type="number"
                                            onChange={(e) => setArea({...area, r: e.target.value})}
                                            value={area.r}
                                            InputProps={{
                                                endAdornment: <InputAdornment position="end">Km</InputAdornment>,
                                            }}
                                        />
                                    </Grid>
                                </Grid>

                                <FormHelperText>You can click on the map to set the center</FormHelperText>
                            </FormControl>

                        </div>
                        <MapGL
                            {...viewport}
                            width="100%"
                            height="calc(100vh - 300px)"
                            mapStyle={MAP_STYLE}
                            onViewportChange={(vp) => setViewport(vp)}
                            mapboxApiAccessToken={TOKEN}
                            onClick={onMapClick}
                            onError={onMapError}
                        >
                            {/*<Pins data={this.props.result} onClick={this._onClickMarker}/>*/}
                            <div>
                                <CanvasOverlay redraw={drawArea}/>
                            </div>

                            <div style={fullscreenControlStyle}>
                                <FullscreenControl/>
                            </div>
                            <div style={navStyle}>
                                <NavigationControl/>
                            </div>
                            <div style={scaleControlStyle}>
                                <ScaleControl/>
                            </div>

                        </MapGL>
                    </div>
                );
            case 2:
                return detailsStep();
            case 3:
                return resultStep();
            default:
                return 'Unknown step';
        }
    }

    const detailsStep = () => {
        return (
            <div className={classes.stepContentContainer}>
                <div className={classes.form}>
                    <Grid container spacing={3}>
                        <Grid xs={12} sm={4} item>
                            <TextField label='Number of Instances' value={details.numberOfInstances}
                                       fullWidth
                                       type="number"
                                       onChange={handleDetailsChanged('numberOfInstances')}/>
                        </Grid>
                        <Grid xs={12} sm={4} item>
                            <TextField label='Average Journey Distance' value={details.averageTravelDistance}
                                       fullWidth
                                       InputProps={{
                                           endAdornment: <InputAdornment position="end">m</InputAdornment>,
                                       }}
                                       type="number"
                                       onChange={handleDetailsChanged('averageTravelDistance')}/>
                        </Grid>
                        <Grid xs={12} sm={4} item>
                            <TextField label='Journey distance standard deviation'
                                       value={details.travelDistanceStandardDeviation}
                                       fullWidth
                                       InputProps={{
                                           endAdornment: <InputAdornment position="end">m</InputAdornment>,
                                       }}
                                       type="number"
                                       onChange={handleDetailsChanged('travelDistanceStandardDeviation')}/>
                        </Grid>
                        <Grid xs={12} sm={4} item>
                            <FormControl className={classes.formControl} fullWidth>
                                <InputLabel id="travel-mode-label">Travel Mode</InputLabel>
                                <Select
                                    labelId="travel-mode-label"
                                    id="travel-mode"
                                    value={details.travelMode}
                                    onChange={handleDetailsChanged('travelMode')}
                                >
                                    {travelModeOptions.map(tm => <MenuItem value={tm}>{tm}</MenuItem>)}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid xs={12} sm={4} item>
                            <TextField label='Update Rate'
                                       value={details.timeInterval}
                                       fullWidth
                                       type="number"
                                       InputProps={{
                                           endAdornment: <InputAdornment position="end">s</InputAdornment>,
                                       }}
                                       onChange={handleDetailsChanged('timeInterval')}/>
                        </Grid>
                        {/*<Grid xs={12} sm={4} item>*/}
                        {/*    <FormControlLabel*/}
                        {/*        control={<Switch value={details.isTwoWay} onChange={handleDetailsChanged('isTwoWay')}/>}*/}
                        {/*        label="Two-way Journey"/>*/}
                        {/*</Grid>*/}
                        {/*{details.isTwoWay && <Grid xs={12} sm={4} item>*/}
                        {/*    <TextField label='Journey distance standard deviation'*/}
                        {/*               value={details.travelDistanceStandardDeviation}*/}
                        {/*               fullWidth*/}
                        {/*               type="number"*/}
                        {/*               onChange={handleDetailsChanged('travelDistanceStandardDeviation')}/>*/}
                        {/*</Grid>*/}
                        {/*}*/}
                        <Grid xs={12} sm={12} item>
                            <MuiPickersUtilsProvider utils={DateFnsUtils}>
                                <Grid container spacing={3}>
                                    <Grid xs={12} sm={4} item>
                                        <KeyboardDatePicker
                                            disableToolbar
                                            variant="inline"
                                            format="MM/dd/yyyy"
                                            margin="normal"
                                            id="date-picker-inline"
                                            label="Date"
                                            fullWidth
                                            value={details.date}
                                            onChange={handleDetailsChanged('date')}
                                            KeyboardButtonProps={{
                                                'aria-label': 'change date',
                                            }}
                                        />
                                    </Grid>
                                    <Grid xs={12} sm={4} item>
                                        <KeyboardTimePicker
                                            margin="normal"
                                            id="time-picker-start"
                                            label="Start time"
                                            fullWidth
                                            value={details.startTime}
                                            onChange={handleDetailsChanged('startTime')}
                                            KeyboardButtonProps={{
                                                'aria-label': 'change time  start',
                                            }}
                                        />
                                    </Grid>
                                    <Grid xs={12} sm={4} item>
                                        <KeyboardTimePicker
                                            margin="normal"
                                            id="time-picker-end"
                                            label="End time"
                                            fullWidth
                                            value={details.endTime}
                                            onChange={handleDetailsChanged('endTime')}
                                            KeyboardButtonProps={{
                                                'aria-label': 'change time end',
                                            }}
                                        />
                                    </Grid>
                                </Grid>
                            </MuiPickersUtilsProvider>
                        </Grid>

                    </Grid>
                </div>
            </div>
        );
    }

    const onClickMarker = (m) => {

    }

    const resultStep = () => {
        return (
            <div className={classes.stepContentContainer}>
                {isLoading && <Backdrop className={classes.backdrop} open={isLoading}>
                    <Typography>{journeyPointsGenerationProgress}</Typography>
                    <br/>
                    <CircularProgress color="inherit"/>
                </Backdrop>}
                <MapGL
                    {...viewport}
                    width="100%"
                    height="calc(100vh - 220px)"
                    mapStyle={MAP_STYLE}
                    onViewportChange={(vp) => setViewport(vp)}
                    mapboxApiAccessToken={TOKEN}
                    onError={onMapError}
                >

                    <div>
                        <CanvasOverlay redraw={drawArea}/>
                    </div>

                    <div>
                        <CanvasOverlay redraw={drawPath}/>
                    </div>

                    <Pins data={journeyPoints} onClick={onClickMarker}/>

                    <div style={fullscreenControlStyle}>
                        <FullscreenControl/>
                    </div>
                    <div style={navStyle}>
                        <NavigationControl/>
                    </div>
                    <div style={scaleControlStyle}>
                        <ScaleControl/>
                    </div>

                </MapGL>
            </div>
        );
    }

    const handleNext = () => {
        if (activeStep == 2) {
            try {
                setIsLoading(true);
                setJourneyPoints([]);
                randomPointsOnPolygon(details.numberOfInstances, area.poly, {color: 'green'}, null, area.bbox,
                    details,
                    (result) => {
                        setJourneyPoints(result);
                        setIsLoading(false);
                        setJourneyPointsGenerationProgress('0');
                    },
                    (progress)=>{
                        setJourneyPointsGenerationProgress(progress+' out of '+details.numberOfInstances);
                    });
            } catch (e) {
                console.log(e);
            }
        }
        if (activeStep == 3) {
            try {
                simulateData();
            } catch (e) {

            }
        }
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleReset = () => {
        setActiveStep(0);
    };


    const simulateData = () => {
        const sensorData = [];
        const {timeInterval} = details;
        let journey_id = 1;
        for (let journey of journeyPoints) {
            const journeySensorData = [];
            let routes = journey.direction.routes;
            let time = new Date(journey.departureTime.getTime());
            const leg = routes[0].legs[0];
            for (let step of leg.steps) {
                let duration = step.duration.value;
                let distance = step.distance.value;
                const polyline = polylineDecoder.decode(step.polyline.points);
                const speedPerSecond = distance / duration;
                const distancePerUnit = speedPerSecond * timeInterval;

                for (let i = 0; i < polyline.length - 1; i++) {
                    let line = lineString([[polyline[i][1], polyline[i][0]], [polyline[i + 1][1], polyline[i + 1][0]]], {name: 'line 1'});
                    const lc = LineChunk;
                    let chunks = lc(line, distancePerUnit, {units: 'meters'});

                    time.addSeconds(timeInterval);
                    for (let chunk of chunks.features) {
                        journeySensorData.push(
                            {
                                point: chunk.geometry.coordinates[1],
                                time: new Date(time.getTime()),
                                speed: speedPerSecond * 3.6
                            }
                        )
                        time = time.addSeconds(timeInterval);
                    }
                }


            }


            const encodePoints = [];
            for (let p of journeySensorData) {
                encodePoints.push([p.point[1], p.point[0]]);
            }

            const generatedPolyline = polylineDecoder.encode(encodePoints);

            sensorData.push({
                journey_id: "journey_" + journey_id++,
                generatedPolyline: generatedPolyline,
                googlePolyline: journey.direction.routes[0].overview_polyline.points,
                departureTime: journey.departureTime,
                destination: journey.destination.geometry.coordinates,
                origin: journey.origin.geometry.coordinates,
                d: journeySensorData
            });
        }

        setSimulatedData(sensorData);

    }


    const exportJson = () => {
        saveFile(JSON.stringify(simulatedData), 'json');
    }

    const generateUserProfile = (pn) => {
        const ups = [];
        for (let i = 0; i < pn; i++) {
            ups.push({
                gender: getRandomInt(1) == 0 ? "male" : "female",
                light_id: 'light_id_' + i,
                light_project_id: 1,
                os: getRandomInt(1) == 0 ? "Android" : "iOS",
                user_id: 'user_id_' + i,
                user_project_id: 1,
                user_email: 'user_' + i + '@email.com',
                mac_address: 'mac_address_' + i,
                age: 10 + getRandomInt(45),
                bike_make:'bike_make_'+getRandomInt(15),
                bike_model:'bike_model_'+getRandomInt(5),
                is_bike_electric: getRandomInt(1) == 0 ? "TRUE" : "FALSE",
            });
        }
        return ups;
    }

    const exportCSV = () => {
        const ups = generateUserProfile(Math.ceil(Math.sqrt(simulatedData.length)));
        let header = [
            "light_id",
            "utc_time",
            "lng",
            "lat",
            "speed",
            "x",
            "y",
            "z",
            "local_time",
            "light_project_id",
            "os",
            "user_id",
            "user_project_id",
            "user_email",
            "mac_address"
            , "age",
            "gender",
            "bike_make",
            "bike_model",
            "is_bike_electric",
            "year",
            "month",
            "day",
            "hour",
            "journey_id"];
        let arrayData = [];
        moment.locale('en-au');
        let jId = 0;
        for (let journey of simulatedData) {
            const up = ups[getRandomInt(ups.length-1)];
            for (let di of journey.d) {
                const mmDate = moment.utc(di.time);
                arrayData.push([
                    up.light_id,
                    moment.utc(di.time).format('DD/MM/YYYY hh:mm:ss A'),
                    di.point[0],
                    di.point[1],
                    di.speed,
                    getRandomInt(100),
                    getRandomInt(100),
                    getRandomInt(100),
                    mmDate.format('DD/MM/YYYY hh:mm:ss A'),
                    up.light_project_id,
                    up.os,
                    up.user_id,
                    up.user_project_id,
                    up.user_email,
                    up.mac_address,
                    up.age,
                    up.gender,
                    up.bike_make,
                    up.bike_model,
                    up.is_bike_electric,
                    mmDate.year(),
                    mmDate.month(),
                    mmDate.day(),
                    mmDate.hour(),
                    journey.journey_id,,
                ])
            }
        }

        let content = header.toString();
        for (let line of arrayData) {
            content += "\n" + line.toString();
        }
        saveFile(content, 'csv');
    }

    const resultSection = () => {
        // <MuiDataTable
        return (
            <div>
                <div style={{minHeight: 'calc(100vh - 220)', maxHeight: 'calc(100vh - 220px)', overflow: 'auto'}}>
                    <JSONTree
                        data={simulatedData}
                        theme={theme} invertTheme={false}/>
                </div>
                <Grid container spacing={3}>
                    <Grid item xs={6}>
                        <Button variant='contained' color='primary' onClick={exportJson}>Download JSON</Button>
                    </Grid>
                    <Grid item xs={6}>
                        <Button variant='contained' color='primary' onClick={exportCSV}>Download CSV</Button>
                    </Grid>
                </Grid>
            </div>
        );
    }


    return (
        <React.Fragment>
            <div className={classes.root}>

                <Stepper activeStep={activeStep}>
                    {steps.map((label, index) => {
                        const stepProps = {};
                        const labelProps = {};
                        return (
                            <Step key={label} {...stepProps}>
                                <StepLabel {...labelProps}>{label}</StepLabel>
                            </Step>
                        );
                    })}
                </Stepper>

                <div>
                    {activeStep === steps.length ? (
                        <div>
                            <Typography className={classes.instructions}>
                                {resultSection()}
                            </Typography>
                            <Button onClick={handleReset} className={classes.button}>
                                Reset
                            </Button>
                        </div>
                    ) : (
                        <div>
                            {getStepContent(activeStep)}
                            <div>
                                <Button disabled={activeStep === 0} onClick={handleBack} className={classes.button}>
                                    Back
                                </Button>

                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleNext}
                                    className={classes.button}
                                >
                                    {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
                                </Button>
                            </div>
                        </div>
                    )}
                </div>

            </div>
        </React.Fragment>
    );
}