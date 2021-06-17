import React, {Component} from 'react';
import MapGL, {Popup, NavigationControl, FullscreenControl, ScaleControl} from 'react-map-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import ControlPanel from './control-panel';
import Pins from './pins';
import CityInfo from './city-info';
import MAP_STYLE from './map-style';
import ReactJson from 'react-json-view'

import CITIES from './cities.json';

export const TOKEN = 'pk.eyJ1IjoiYWhhczM2IiwiYSI6ImNrNW95MjkwNzAyMmMza3F4bHNsZnJ1MmwifQ.N9_HUZjsstO38vnTLd1ntQ'; // Set your mapbox token here

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

export default class ResultMap extends Component {
    constructor(props) {
        super(props);
        this.state = {
            viewport: {
                latitude: 37.785164,
                longitude: -100,
                zoom: 9,
                bearing: 0,
                pitch: 0
            },
            popupInfo: {open: false, info: null}
        };
    }

    componentDidUpdate(prevProps, prevState) {

        const result = this.props.result;
        if (this.props.result != prevProps.result) {
            let lat = 0;
            let lng = 0;
            let counter = 0;
            for (let item in result) {
                try {
                    let entities = result[item].results;
                    if (entities) {
                        for (let instance in entities) {
                            try {
                                lat += parseFloat(entities[instance].geo.latitude);
                                lng += parseFloat(entities[instance].geo.longitude);
                                counter++;
                            } catch (e) {

                            }

                        }
                    } else {
                        try {
                            lat += parseFloat(result[item].geo.latitude);
                            lng += parseFloat(result[item].geo.longitude);
                            counter++;
                        } catch (e) {

                        }
                    }

                } catch (e) {
                }
            }
            if (counter > 0) {
                let viewport = this.state.viewport;
                viewport.latitude = lat / counter;
                viewport.longitude = lng / counter;
                this.setState({viewport});
            }

        }
    }

    _updateViewport = viewport => {
        this.setState({viewport});
    };

    _onClickMarker = info => {
        this.setState({popupInfo: {open: true, info: info}});
    };

    _renderPopup() {
        const {popupInfo} = this.state;

        return (
            popupInfo && (
                <Popup
                    tipSize={5}
                    anchor="top"
                    longitude={parseFloat(popupInfo.geo.longitude)}
                    latitude={parseFloat(popupInfo.geo.latitude)}
                    closeOnClick={false}
                    onClose={() => this.setState({popupInfo: null})}
                >
                    <CityInfo info={popupInfo}/>
                </Popup>
            )
        );
    }

    _handleDialogClose = () => {
        this.setState({popupInfo: {open: false, info: {}}});
    }


    render() {
        const {viewport, popupInfo} = this.state;

        return (
            <React.Fragment>
                <Dialog style={{zIndex: 2000}} maxWidth={'lg'} fullWidth={true} onClose={this._handleDialogClose}
                        aria-labelledby="Marker Info" open={popupInfo.open}>
                    <DialogTitle
                        id="marker-dialog-title">{popupInfo.info != null ? popupInfo.info['@type'] : ''}</DialogTitle>
                    <DialogContent style={{height: 'calc(100vh - 50px)', backgroundColor: '#272823'}}>
                        <ReactJson src={popupInfo.info} theme='monokai' iconStyle='triangle'/>
                    </DialogContent>
                </Dialog>
                <MapGL
                    {...viewport}
                    width="100%"
                    height="100%"
                    mapStyle={MAP_STYLE}
                    onViewportChange={this._updateViewport}
                    mapboxApiAccessToken={TOKEN}
                >
                    <Pins data={this.props.result} onClick={this._onClickMarker}/>


                    <div style={fullscreenControlStyle}>
                        <FullscreenControl/>
                    </div>
                    <div style={navStyle}>
                        <NavigationControl/>
                    </div>
                    <div style={scaleControlStyle}>
                        <ScaleControl/>
                    </div>

                    {/*<ControlPanel containerComponent={this.props.containerComponent} />*/}
                </MapGL>
            </React.Fragment>
        );
    }
}