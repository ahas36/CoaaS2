import React, {PureComponent} from 'react';
import {Marker} from 'react-map-gl';

const ICON = `M20.2,15.7L20.2,15.7c1.1-1.6,1.8-3.6,1.8-5.7c0-5.6-4.5-10-10-10S2,4.5,2,10c0,2,0.6,3.9,1.6,5.4c0,0.1,0.1,0.2,0.2,0.3
  c0,0,0.1,0.1,0.1,0.2c0.2,0.3,0.4,0.6,0.7,0.9c2.6,3.1,7.4,7.6,7.4,7.6s4.8-4.5,7.4-7.5c0.2-0.3,0.5-0.6,0.7-0.9
  C20.1,15.8,20.2,15.8,20.2,15.7z`;

const SIZE = 20;


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

        let typeLength = [];

        const generatePin = (item, index) => {
            try {
                const {origin, destination} = item;

                return <React.Fragment>
                    <Marker key={`marker-origin-${index}`} longitude={origin.geometry.coordinates[0]}
                            latitude={origin.geometry.coordinates[1]}>
                        <svg
                            height={SIZE}
                            viewBox="0 0 24 24"
                            style={{
                                cursor: 'pointer',
                                fill: 'green',
                                stroke: 'none',
                                transform: `translate(${-SIZE / 2}px,${-SIZE}px)`
                            }}
                            onClick={() => onClick(item)}
                        >
                            <path d={ICON}/>
                        </svg>
                    </Marker>
                    <Marker key={`marker-dest-${index}`} longitude={destination.geometry.coordinates[0]}
                            latitude={destination.geometry.coordinates[1]}>
                        <svg
                            height={SIZE}
                            viewBox="0 0 24 24"
                            style={{
                                cursor: 'pointer',
                                fill: 'blue',
                                stroke: 'none',
                                transform: `translate(${-SIZE / 2}px,${-SIZE}px)`
                            }}
                            onClick={() => onClick(item)}
                        >
                            <path d={ICON}/>
                        </svg>
                    </Marker>
                </React.Fragment>
                    ;
            } catch (ex) {
                return;
            }
        }


        return (
            <React.Fragment>
                {data != null && data.map((item, index) => (
                    generatePin(item, index)
                ))}
            </React.Fragment>
        )
    }
}