import React, {Component} from "react";
// Import Highcharts
import Highcharts from "highcharts/highstock";
//import HighchartsReact from "./HighchartsReact.min.js";
import HighchartsReact from "highcharts-react-official";

class ChartsHeart extends Component {

    constructor(props) {
        super(props);
        this.state = {

        };
        this.child = React.createRef();
    }

    componentDidMount() {
        var self = this;
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

    render() {

        let { zone1sec = 0, zone2sec = 0, zone3sec = 0, zone4sec = 0, zone5sec = 0, maxHr = 0 } = this.props.hrZones;

        let total = zone1sec + zone2sec + zone3sec + zone4sec + zone5sec;


        const options = {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Heart rate zones'
            },
            subtitle: {
                text: ''
            },
            accessibility: {
                announceNewData: {
                    enabled: true
                }
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Total percent'
                }

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}%'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>Between:  {point.description} bpm'
            },

            series: [
                {
                    name: "Zones",
                    colorByPoint: true,
                    data: [
                        {
                            name: "Zone 5<br>" + this.getTime(zone5sec),
                            y: zone5sec / total * 100,
                            drilldown: "Zone 5",
                            color: "#9b1f00",
                            description: Math.round(maxHr *.9) + 1 + " - " + maxHr
                        },
                        {
                            name: "Zone 4<br>" + this.getTime(zone4sec),
                            y: zone4sec / total* 100,
                            drilldown: "Zone 4",
                            color: "#9b6100",
                            description: Math.round(maxHr *.8) + 1 + " - " + Math.round(maxHr *.9)
                        },
                        {
                            name: "Zone 3<br>" + this.getTime(zone3sec),
                            y: zone3sec / total* 100,
                            drilldown: "Zone 3",
                            color: "#0d9b00",
                            description: Math.round(maxHr *.7) + 1 + " - " + Math.round(maxHr *.8)
                        },
                        {
                            name: "Zone 2<br>" + this.getTime(zone2sec),
                            y: zone2sec / total* 100,
                            drilldown: "Zone 2",
                            color: "#00499b",
                            description: (Math.round(maxHr *.6) + 1) + " - " + Math.round(maxHr *.7)
                        },
                        {
                            name: "Zone 1<br>" + this.getTime(zone1sec),
                            y: zone1sec / total* 100,
                            drilldown: "Zone 1",
                            color: "#a3a9b1",
                            description: Math.floor(maxHr *.5) + " - " + Math.round(maxHr *.6)
                        }
                    ]
                }
            ]
        };



        return(
            <div id="container">
                <HighchartsReact highcharts={Highcharts} options={options}/>
            </div>
        );
    }
};

export default ChartsHeart;