import React,{Component} from 'react';
import {Link} from 'react-router-dom';
import PropTypes from "prop-types";
import { withRouter } from "react-router";
import ErrorAlerts from "../alerts/ErrorAlerts";
import {fetchHandler} from "../scripts/fetchHandler";

class SegmentTable extends Component {

    static propTypes = {
        match: PropTypes.object.isRequired,
        location: PropTypes.object.isRequired,
        history: PropTypes.object.isRequired
    };

    constructor() {
        super();
        this.state = {segments: [], errorMsg: false};
    }

    componentDidMount() {
       this.refresh();
    }

    refresh() {

        this.setState({loading: true })
        fetchHandler("api/segment/all", null,(json) => {
            console.log(json);
            this.setState({segments: json, loading: false })
        }, (json) =>  this.setState({errorMsg: json, hasErrors: true, loading: false }))
    }


    render() {
        const { match, location, history } = this.props;

        return (
            <div class="container">
                <h2>Segment data</h2>
                <ErrorAlerts errorMsg={this.state.errorMsg} />

                {
                    this.state.segments.map((obj, i) => (

                        <div>
                            <div className="card">
                                <div className="card-header">
                                    <b>{obj.name}</b>
                                </div>
                                <div className="card-body">
                                    <blockquote className="blockquote mb-0">

                                        <footer className="blockquote-footer">{obj.name}</footer>
                                        <div className="container">
                                            <Link to={`${match.url}/${obj.id}`}>{obj.name}</Link>
                                        </div>
                                    </blockquote>
                                </div>
                            </div>
                            <div>&nbsp;</div>
                        </div>
                    ))
                }

            </div>
        )
    }
}
export default withRouter(SegmentTable);

