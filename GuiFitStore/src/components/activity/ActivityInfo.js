import React,{Component} from 'react';
import PropTypes from "prop-types";
import { withRouter } from "react-router";
import ErrorAlerts from "../alerts/ErrorAlerts";
import {fetchHandler} from "../scripts/fetchHandler";
import MapWithAMarker from "../map/MapContainer"
import Charts from "../charts/Charts"
import ChartsHeart from "../charts/ChartsHeart"
import {Link} from "react-router-dom";
import Moment from 'moment';


let jsonData = [];

class ActivityInfo extends Component {

    static propTypes = {
        match: PropTypes.object.isRequired,
        location: PropTypes.object.isRequired,
        history: PropTypes.object.isRequired
    };

    constructor() {
        super();
        this.state = {
            training: false,
            records: [],
            altitudes: [],
            altrange: { min: null, max: null },
            speed: [],
            speedrange: { min: 0, max: null },
            heartrate: [],
            hrrange: { min: null, max: null },
            bounds: new window.google.maps.LatLngBounds(),
            map: null,
            errorMsg: false,
            loading: false,
            commentLink: false,
            markerLat: null,
            markerLng: null,
            textVal: '',
            zoomDomain: { x: [new Date(1990, 1, 1), new Date(2009, 1, 1)] },
            optionsAlt: null,
            optionsSpeed: null,
            optionsHr: null
            };

        this.child = React.createRef();

    }

    callIndex = (index) => {
        //console.log("called index = " + index);
        let lat = this.state.training.records[index].lat / 11930465;
        let lon = this.state.training.records[index].lon / 11930465;
        //console.log(lat + " " + lon);
        this.child.current.setMarker({lat: lat, lng: lon});
    };

    getOid(){
        var timestamp = (new Date().getTime() / 1000 | 0).toString(16);
        return timestamp + 'xxxxxxxxxxxxxxxx'.replace(/[x]/g, function() {
            return (Math.random() * 16 | 0).toString(16);
        }).toLowerCase();
    }

    getTime(seconds) {
        let date = new Date(seconds * 1000);
        let hh = date.getUTCHours();
        let mm = date.getUTCMinutes();
        let ss = date.getSeconds();

        if (hh < 10) {hh = "0"+hh;}
        if (mm < 10) {mm = "0"+mm;}
        if (ss < 10) {ss = "0"+ss;}

        return hh+":"+mm+":"+ss;
    }


    callRange = (min, max) => {
        //console.log(min + " - " + max);
        let coordinates = [];
        let records = [];
        let distance = this.state.training.records[max].distance - this.state.training.records[min].distance;
        let dst = null;
        for(let i = min; i < max; i++){
            let lat = this.state.training.records[i].lat / 11930465;
            let lon = this.state.training.records[i].lon / 11930465;
            let dist = this.state.training.records[i].distance;

            if(lat !== 0 && lon !== 0) {
                coordinates.push([lat, lon]);
                if(dst === null) {
                    dst = dist;
                    dist = 0;
                } else {
                    dist -= dst;
                }
                records.push({ altitude: this.state.training.records[i].altitude, distance: dist });
            }
        }
        let segment = {
            "_id": {
                "$oid": this.getOid()
            },
            "name": "Example",
            "distance": distance,
            "records": records,
            "location": {
                "type": "LineString",
                coordinates: coordinates
            }
        };

        console.log(JSON.stringify(segment));
    };

    componentDidMount() {
        let topicId = this.props.match.params.topicId;
        this.setState({loading: true});


        fetchHandler('/api/data/findbyid/' + topicId, null,(json) => {

            let altrange = { min: null, max: null };
            let hrrange = { min: null, max: null };
            let speedrange = { min: 0, max: null };
            let records = [];
            let altitudes = [];
            let speed = [];
            let heartrate = [];
            let bounds = new window.google.maps.LatLngBounds();
            jsonData = json.records;

            if(json.records !== undefined && json.records !== null){

                json.records.map((record, index) => {
                    //console.log(index);
                    let distance = (record.distance / 1000);

                    if(record.lat !== null && record.lat !== 0) {
                        records.push({ lat: record.lat / 11930465, lng: record.lon / 11930465 });
                    }

                    if(record.altitude !== null) {
                        if(altrange.min == null || record.altitude < altrange.min ) {
                            altrange.min = Math.floor(record.altitude);
                        }
                        if(altrange.max == null || record.altitude > altrange.max ) {
                            altrange.max = Math.round(record.altitude);
                        }
                    }
                    if(record.altitude !== null) {
                        //altitudes.push([(record.distance / 1000), record.altitude, index]);
                        altitudes.push([distance, record.altitude, index]);
                    }
                    if(record.speed !== null) {
                        const spd = Math.round((record.speed * 3.6) * 100) / 100;
                        speed.push([distance, spd]);
                    }
                    if(record.speed !== null) {
                        const spd = Math.round((record.speed * 3.6) * 100) / 100;
                        if(speedrange.max == null || spd > speedrange.max ) {
                            speedrange.max = Math.round(spd);
                        }
                    }
                    if(record.heartRate !== null) {
                        if(hrrange.min == null || record.heartRate < hrrange.min ) {
                            hrrange.min = record.heartRate;
                        }
                        if(hrrange.max == null || record.heartRate > hrrange.max ) {
                            hrrange.max = record.heartRate;
                        }
                    }
                    if(record.heartRate !== null) {
                        heartrate.push([distance, record.heartRate]);
                    }

                });

                altrange.max+=5;
                altrange.min-=5;

                hrrange.max+=5;
                hrrange.min-=5;

                bounds.extend(new window.google.maps.LatLng(json.geoPolygon.coordinates[0][0][0], json.geoPolygon.coordinates[0][0][1]));
                bounds.extend(new window.google.maps.LatLng(json.geoPolygon.coordinates[0][1][0], json.geoPolygon.coordinates[0][1][1]));
                bounds.extend(new window.google.maps.LatLng(json.geoPolygon.coordinates[0][2][0], json.geoPolygon.coordinates[0][2][1]));
                bounds.extend(new window.google.maps.LatLng(json.geoPolygon.coordinates[0][3][0], json.geoPolygon.coordinates[0][3][1]));

                console.log(json.geoPolygon.coordinates[0][0][1]);
                console.log(bounds);
            }

            this.setState({loading: false, training: json, records: records, altitudes: altitudes, altrange: altrange, speed: speed, speedrange: speedrange,
                heartrate: heartrate, hrrange: hrrange, bounds: bounds
            })
        }, (json) =>  this.setState({loading: false, errorMsg: json}));
    }

    render() {

        const { records, bounds, altitudes, speed, heartrate, altrange, speedrange, hrrange } = this.state;

        let { title, description, user, garminSession, hrZones, date, segmentInfoList=[], segmentInfos=[] } = this.state.training;

        const map = new Map();
        segmentInfos.map(value => { map.set(value.id, value.name) });

        let firstLastName = user !== undefined ? user.firstName + " " + user.lastName : "";

        if(garminSession === undefined) {
            garminSession = {};
        }

        let { totalDistance = "", totalAscent = "", totalMovingTime = "", maxSpeed = "", avgSpeed = "",
            maxHeartRate = "", avgHeartRate = "", maxCadence = "", avgCadence = "", avgTemperature = "", maxTemperature = "" , totalElapsedTime = ""}    = garminSession;

        if(totalDistance > 1000) {
            totalDistance = Number.parseFloat((totalDistance / 1000)).toPrecision(4) + " km";
        } else {
            totalDistance = Math.floor(totalDistance) + " m";
        }

        totalMovingTime = this.getTime(totalMovingTime);
        totalElapsedTime = this.getTime(totalElapsedTime);

        maxSpeed = Math.round((maxSpeed * 3.6) * 100) / 100;
        avgSpeed = Math.round((avgSpeed * 3.6) * 100) / 100;

        return (
            <div className="container">
                <ErrorAlerts errorMsg={this.state.errorMsg}/>

                <div className="card">
                    <h3 className="card-header">{firstLastName} - {title}</h3>
                    <div className="card-body">


                        <div className="container">
                            <div className="row">
                                <div className="col-sm">
                                    <p className="card-text">{Moment(date).format('DD MMMM YYYY HH:MM')}</p>
                                    <p className="card-text">{description}</p>

                                    <div className="container">
                                        <div className="row">
                                            <div className="col"><b>{totalDistance}</b></div>
                                            <div className="col"><b>{totalMovingTime}</b></div>
                                            <div className="col"><b>{totalAscent} m</b></div>
                                            <div className="w-100"></div>
                                            <div className="col">Distance</div>
                                            <div className="col">Moving Time </div>
                                            <div className="col">Elevation</div>
                                        </div>
                                    </div>

                                    <div>&nbsp;</div>

                                    <small>
                                    <table className="table">
                                        <thead>
                                        <tr>
                                            <th scope="col"></th>
                                            <th scope="col">Avg</th>
                                            <th scope="col">Max</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td>Speed</td>
                                            <td>{avgSpeed}km/h</td>
                                            <td>{maxSpeed}km/h</td>
                                        </tr>
                                        <tr>
                                            <td>Heart Rate</td>
                                            <td>{avgHeartRate}bpm</td>
                                            <td>{maxHeartRate}bpm</td>
                                        </tr>
                                        <tr>
                                            <td>Cadence</td>
                                            <td>{avgCadence}</td>
                                            <td>{maxCadence}</td>
                                        </tr>
                                        <tr>
                                            <td>Temperature</td>
                                            <td>{avgTemperature}℃</td>
                                            <td>{maxTemperature}℃</td>
                                        </tr>
                                        <tr>
                                            <td>Elapsed Time</td>
                                            <td>{totalElapsedTime}</td>
                                            <td></td>
                                        </tr>

                                        </tbody>
                                    </table>
                                    </small>

                                </div>
                                <div className="col-sm">
                                    {hrZones !== undefined && hrZones !== null ? <ChartsHeart hrZones={hrZones}/> : ""}
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                {records.length > 0 ? <MapWithAMarker ref={this.child} trackData={records} bounds={bounds}/> : ""}

                <Charts altitudes={altitudes} speed={speed} heartrate={heartrate} altrange={altrange} speedrange={speedrange} hrrange={hrrange}
                callIndex={this.callIndex} callRange={this.callRange} />


                <div>&nbsp;</div>
                <h3>Segments</h3>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">name</th>
                        <th scope="col">avg speed</th>
                        <th scope="col">max speed</th>
                        <th scope="col">avg bpm</th>
                        <th scope="col">max bpm</th>
                        <th scope="col">time</th>
                    </tr>
                    </thead>
                    <tbody>

                    {
                        segmentInfoList.map((obj, i) => {
                            const avgSpeed = Math.round((obj.avgSpeed * 3.6) * 100) / 100;
                            const maxSpeed = Math.round((obj.maxSpeed * 3.6) * 100) / 100;

                            return (<tr key={i}>
                                <th scope="row">{i + 1}</th>
                                <td><Link to={`/Segments/${obj.segmentId}`}>{map.get(obj.segmentId)}</Link></td>
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
export default withRouter(ActivityInfo);


