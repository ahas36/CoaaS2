import React, {PureComponent} from 'react';
import {Marker} from 'react-map-gl';

const ICON = `M20.2,15.7L20.2,15.7c1.1-1.6,1.8-3.6,1.8-5.7c0-5.6-4.5-10-10-10S2,4.5,2,10c0,2,0.6,3.9,1.6,5.4c0,0.1,0.1,0.2,0.2,0.3
  c0,0,0.1,0.1,0.1,0.2c0.2,0.3,0.4,0.6,0.7,0.9c2.6,3.1,7.4,7.6,7.4,7.6s4.8-4.5,7.4-7.5c0.2-0.3,0.5-0.6,0.7-0.9
  C20.1,15.8,20.2,15.8,20.2,15.7z`;

const SIZE = 20;

// Important for perf: the markers never change, avoid rerender when the map viewport changes
export default class Pins extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            viewport: {
                latitude: 37.785164,
                longitude: -100,
                zoom: 3.5,
                bearing: 0,
                pitch: 0
            },
            popupInfo: null
        };
    }

    render() {
        const {data, onClick} = this.props;

        const colours = ['#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4', '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1', '#000075', '#808080']
        let typeLength = [];

        const parse = (data) =>
        {
            let result = [];
            typeLength = [];
            let lastValue = 0;
            for(let item in data)
            {
                if(data[item].results!=null && data[item].results.length)
                {
                    typeLength.push(lastValue + data[item].results.length);
                    lastValue = data[item].results.length;
                    result.push(...data[item].results);
                }else{
                    typeLength.push(lastValue + 1);
                    lastValue = 1;
                    result.push(data[item]);
                }
            }
            return result;
        }

        const generatePin = (item,index) =>{
            try{
                return <Marker key={`marker-${index}`} longitude={parseFloat(item.geo.longitude)} latitude={parseFloat(item.geo.latitude)}>
                    <svg
                        height={SIZE}
                        viewBox="0 0 24 24"
                        style={{
                            cursor: 'pointer',
                            fill: getColour(index),
                            stroke: 'none',
                            transform: `translate(${-SIZE / 2}px,${-SIZE}px)`
                        }}
                        onClick={() => onClick(item)}
                    >
                        <path d={ICON}/>
                    </svg>
                </Marker>;
            }catch(ex){
                return;
            }
        }

        const getColour = (index) =>
        {
            let colourIndex = 0;
            while (index > typeLength[colourIndex])
            {
                colourIndex++;
            }
            return colours[colourIndex];
        }

        return (
            <React.Fragment>
                {data!=null && parse(data).map((item, index) => (
                    generatePin(item,index)
                    ))}
            </React.Fragment>
        )
    }
}