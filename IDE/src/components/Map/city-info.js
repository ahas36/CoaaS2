import React, {PureComponent} from 'react';
import ReactJson from 'react-json-view'

export default class CityInfo extends PureComponent {
    render() {
        const {info} = this.props;
        return (
            <div style={{width:'200px', height:'160px', overflow:'auto'}}>
                <ReactJson src={info} theme='monokai' iconStyle='triangle'/>
            </div>
        );
    }
}