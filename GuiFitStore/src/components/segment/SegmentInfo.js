import React,{Component} from 'react';
import PropTypes from "prop-types";
import { withRouter } from "react-router";
import ErrorAlerts from "../alerts/ErrorAlerts";
import {fetchHandler} from "../scripts/fetchHandler";
import MapWithAMarker from "../map/MapContainer"
import Moment from 'moment';
import {Link} from "react-router-dom";

let jsonData = [];

class SegmentInfo extends Component {

    static propTypes = {
        match: PropTypes.object.isRequired,
        location: PropTypes.object.isRequired,
        history: PropTypes.object.isRequired
    };

    constructor() {
        super();
        this.state = {
            records: [],
            bounds: new window.google.maps.LatLngBounds(),
            map: null,
            errorMsg: false,
            loading: false,
            name: "",
            topSegmentList: [],
            distance: null,
            userInfos: []
            };

        this.child = React.createRef();

    }

    getTime(seconds) {
        let date = new Date(seconds * 1000);
        let hh = date.getUTCHours();
        let mm = date.getUTCMinutes();
        let ss = date.getSeconds();

        if (hh < 10) {hh = "0"+hh;}
        if (mm < 10) {mm = "0"+mm;}
        if (ss < 10) {ss = "0"+ss;}

        if(hh === "00") {
            hh=""
        } else {
            hh+=":";
        }

        return hh+mm+":"+ss;
    }

    componentDidMount() {
        let topicId = this.props.match.params.topicId;
        this.setState({loading: true});


        fetchHandler('/api/segment/' + topicId, null,(json) => {

            console.log(json);

            let records = [];
            let bounds = new window.google.maps.LatLngBounds();
            jsonData = json.records;

            if(json.location.coordinates !== undefined && json.location.coordinates !== null) {

                let minLat, maxLat, minLon, maxLon = null;

                json.location.coordinates.map((coords, index) => {
                    //console.log(coords);
                    if(minLat == null || coords[0] < minLat) {
                        minLat = coords[0];
                    }
                    if(minLon == null || coords[1] < minLon) {
                        minLon = coords[1];
                    }

                    if(maxLat == null || coords[0] > maxLat) {
                        maxLat = coords[0];
                    }
                    if(maxLon == null || coords[1] > maxLon) {
                        maxLon = coords[1];
                    }

                    records.push({ lat: coords[0], lng: coords[1] });
                });

                console.log(minLat, maxLat, minLon, maxLon);

                bounds.extend(new window.google.maps.LatLng(minLat, minLon));
                bounds.extend(new window.google.maps.LatLng(maxLat, minLon));
                bounds.extend(new window.google.maps.LatLng(maxLat, maxLon));
                bounds.extend(new window.google.maps.LatLng(minLat, maxLon));

                console.log(bounds);
            }

            if(json.topSegmentInfoList === null) {
                json.topSegmentInfoList = [];
            }

            this.setState({loading: false, name: json.name, distance: json.distance, records: records, bounds: bounds, topSegmentList: json.topSegmentInfoList, userInfos: json.userInfos})

        }, (json) =>  this.setState({loading: false, errorMsg: json}));
    }

    render() {

        let { name, distance, records, bounds, topSegmentList = [], userInfos = []} = this.state;

        const map = new Map();
        userInfos.map(value => { map.set(value.id, value.firstName + " " + value.lastName) });

        if(distance > 1000) {
            distance = Number.parseFloat((distance / 1000)).toPrecision(4) + " km";
        } else {
            distance = Math.floor(distance) + " m";
        }
        return (
            <div className="container">
                    <ErrorAlerts errorMsg={this.state.errorMsg}/>
                    <h3>{name}</h3>
                    <ul className="list-group">
                        <li className="list-group-item"><b>Segment:</b> {name}</li>
                        <li className="list-group-item"><b>Distance:</b> {distance}</li>
                        <li className="list-group-item"><b>Dot:</b> - is where finish</li>
                    </ul>

                {records.length > 0 ? <MapWithAMarker ref={this.child} trackData={records} bounds={bounds}/> : ""}

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">user</th>
                        <th scope="col">date</th>
                        <th scope="col">avg speed</th>
                        <th scope="col">max speed</th>
                        <th scope="col">avg bpm</th>
                        <th scope="col">max bpm</th>
                        <th scope="col">time</th>
                    </tr>
                    </thead>
                    <tbody>

                    {
                        topSegmentList.map((obj, i) => {
                            const avgSpeed = Math.round((obj.avgSpeed * 3.6) * 100) / 100;
                            const maxSpeed = Math.round((obj.maxSpeed * 3.6) * 100) / 100;

                            return (<tr key={i}>
                                <th scope="row">{i + 1}</th>
                                <td><Link to={`/Activities/${obj.trainingId}`}>{map.get(obj.userId)}</Link></td>
                                <td>{Moment(obj.date).format('DD MMMM YYYY')}</td>
                                <td>{avgSpeed}km/h</td>
                                <td>{maxSpeed}km/h</td>
                                <td>{obj.avgHeart}bpm</td>
                                <td>{obj.maxHeart}bpm</td>
                                <td>{this.getTime(obj.time)}</td>
                            </tr>);
                        })
                    }


                    </tbody>
                </table>

            </div>

        );

    }

}
export default withRouter(SegmentInfo);


