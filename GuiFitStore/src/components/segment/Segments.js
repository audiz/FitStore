import React,{Component} from 'react';
import {BrowserRouter as Router, Link, Switch, Route, useParams, useRouteMatch} from 'react-router-dom';
import SegmentTable from "./SegmentTable";
import SegmentInfo from "./SegmentInfo";
import { withRouter } from "react-router";

class Segments extends Component {
    componentDidMount() {
        document.title = 'TrainingsTable'
    }

    render() {
        const { match, location, history } = this.props;
        return ( <div>
            <Switch>
                <Route path="/Segments" exact={true} component={SegmentTable} />
                <Route path="/Segments/:topicId" exact={true} component={SegmentInfo} ></Route>
            </Switch>

        </div>)
    }
}
export default withRouter(Segments);