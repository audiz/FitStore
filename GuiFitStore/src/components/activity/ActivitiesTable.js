import React,{Component} from 'react';
import {Link} from 'react-router-dom';
import PropTypes from "prop-types";
import { withRouter } from "react-router";
import ErrorAlerts from "../alerts/ErrorAlerts";
import Select from "./Select";
import {fetchHandler} from "../scripts/fetchHandler";
import Moment from 'moment';

class ActivitiesTable extends Component {

    static propTypes = {
        match: PropTypes.object.isRequired,
        location: PropTypes.object.isRequired,
        history: PropTypes.object.isRequired
    };

    constructor() {
        super();
        this.state = {trainings: [], errorMsg: false};
    }

    componentDidMount() {
       this.refresh();
    }

    refresh() {

        this.setState({loading: true })
        fetchHandler("api/data/all", null,(json) => {
            console.log(json);
            this.setState({trainings: json, loading: false })
        }, (json) =>  this.setState({errorMsg: json, hasErrors: true, loading: false }))
    }


    render() {
        const { match, location, history } = this.props;

        return (
            <div class="container">
                <h2>Training data</h2>
                <ErrorAlerts errorMsg={this.state.errorMsg} />

                {
                    this.state.trainings.map((obj, i) => {

                        let {totalDistance, totalAscent, totalMovingTime} = obj.garminSession;
                        if(totalDistance > 1000) {
                            totalDistance = Number.parseFloat((totalDistance / 1000)).toPrecision(4) + " km";
                        } else {
                            totalDistance = Math.floor(totalDistance) + " m";
                        }

                        let seconds = totalMovingTime;
                        let date = new Date(seconds * 1000);
                        let hh = date.getUTCHours();
                        let mm = date.getUTCMinutes();
                        let ss = date.getSeconds();
                        if (hh < 10) {hh = "0"+hh;}
                        if (mm < 10) {mm = "0"+mm;}
                        if (ss < 10) {ss = "0"+ss;}
                        totalMovingTime = hh+":"+mm+":"+ss;

                        return (
                        <div>
                            <div className="card">
                                <div className="card-header">
                                    <b>{obj.user.firstName} {obj.user.lastName}</b> {Moment(obj.date).format('DD MMMM YYYY HH:MM')}
                                </div>
                                <div className="card-body">
                                    <blockquote className="blockquote mb-0">
                                        <Link to={`${match.url}/${obj.id}`}>{obj.title}</Link>
                                        <footer className="blockquote-footer">{obj.description}</footer>
                                        <div className="container">
                                            <div className="row">
                                                <div className="col">Distance</div>
                                                <div className="col">Elev Gain</div>
                                                <div className="col">Time</div>
                                                <div className="w-100"></div>
                                                <div className="col">{totalDistance}</div>
                                                <div className="col">{totalAscent} m</div>
                                                <div className="col">{totalMovingTime}</div>
                                            </div>
                                            <Link to={`${match.url}/${obj.id}`}>
                                            <img src={"https://maps.googleapis.com/maps/api/staticmap?size=544x218&maptype=roadmap&path=color:0x000000ff|weight:2|enc:" + obj.polyUrl + "&format=jpg&key=google_api_key"} width={544} height={218}/>
                                            </Link>
                                        </div>
                                    </blockquote>
                                </div>
                            </div>
                            <div>&nbsp;</div>


                        </div>



                    )})
                }


            </div>
        )
    }
}
export default withRouter(ActivitiesTable);

