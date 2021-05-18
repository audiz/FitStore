import React, {Component} from "react";
// Import Highcharts
import Highcharts from "highcharts/highstock";
//import HighchartsReact from "./HighchartsReact.min.js";
import HighchartsReact from "highcharts-react-official";

(function(H) {
    H.Pointer.prototype.reset = function() {
        return undefined;
    };

    /**
     * Highlight a point by showing tooltip, setting hover state and draw crosshair
     */
    H.Point.prototype.highlight = function(event) {
        event = this.series.chart.pointer.normalize(event);
        this.onMouseOver(); // Show the hover marker
        //this.series.chart.tooltip.refresh(this); // Show the tooltip
        //this.series.chart.xAxis[0].drawCrosshair(event, this); // Show the crosshair
    };

    H.syncExtremes = function(e) {
        var thisChart = this.chart;
        if (e.trigger !== "syncExtremes") {
            // Prevent feedback loop
            Highcharts.each(Highcharts.charts, function(chart) {
                if (chart && chart !== thisChart) {
                    if (chart.xAxis[0].setExtremes) {
                        // It is null while updating
                        chart.xAxis[0].setExtremes(e.min, e.max, undefined, false, {
                            trigger: "syncExtremes"
                        });
                    }
                }
            });
        }
    };
})(Highcharts);

let jsonData = [];

class Charts extends Component {

    constructor(props) {
        super(props);
        this.state = {

        };
        this.child = React.createRef();
    }

    componentDidMount() {
        var self = this;
        ["mousemove", "touchmove", "touchstart"].forEach(function(eventType) {
            document
                .getElementById("container")
                .addEventListener(eventType, function(e) {
                    var chart, point, i, event;
                    for (i = 0; i < Highcharts.charts.length; i = i + 1) {
                        chart = Highcharts.charts[i];
                        //console.log(e);
                        if (chart && chart.title.textStr !== "Heart rate zones") {
                            // Find coordinates within the chart
                            event = chart.pointer.normalize(e);

                            // Get the hovered point
                            chart.series.forEach(series => {
                                const point = series.searchPoint(event, true);
                                if (point) {

                                    if(chart.title.textStr === "Speed") {
                                       // self.setState({ markerLat: jsonData[point.category].lat / 11930465, markerLng: jsonData[point.category].lon / 11930465})
                                        //console.log(point.index);
                                        self.props.callIndex(point.index);
                                    }
                                    try {
                                        point.highlight(e);
                                    } catch (err) {
                                        // pass;
                                    }
                                }
                            });
                        }
                    }
                });
        });
    }

    render() {

        const {altitudes, speed, heartrate, altrange, speedrange, hrrange} = this.props;
        let self = this;

        const optionsAlt = {
            chart: {
                marginLeft: 40, // Keep all charts left aligned
                spacingTop: 20,
                spacingBottom: 20,


            },
            credits: { enabled: false },
            legend: { enabled: false },
            plotOptions: {
                line: { marker: { enabled: false } },
                column: { pointWidth: 6 }
            },
            series: [{
                data: altitudes,
                name: "Elevation",
                type: "area",
                color: Highcharts.getOptions().colors[1],
                fillOpacity: 0.3,
                tooltip: {
                    valueSuffix: ' m'
                }
            }],
                title: {
                text: "Elevation",
                    align: 'left',
                    margin: 0,
                    x: 30
            },
            tooltip: {
                positioner: function () {
                    return {
                        // right aligned
                        x: this.chart.chartWidth - this.label.width,
                        y: 10 // align to title
                    };
                },
                borderWidth: 0,
                    backgroundColor: 'none',
                    pointFormat: '{point.y}',
                    headerFormat: '',
                    shadow: false,
                    style: {
                    fontSize: '18px'
                },
                valueDecimals: altitudes.valueDecimals
            },
            xAxis: {
                min: 0,
                crosshair: true,
                events: {
                    setExtremes: function(e) {
                        Highcharts.syncExtremes(e);
                    }
                },
                labels: {
                    format: '{value} km'
                }
            },
            yAxis: {
                tickPositions: [altrange.min, altrange.max],
                startOnTick: false,
                min: altrange.min,
                max: altrange.max,
                title: {
                    text: null
                }
            }
        };

        const optionsSpeed = {
            chart: {
                marginLeft: 40, // Keep all charts left aligned
                spacingTop: 20,
                spacingBottom: 20,
                events: {
                    selection: function (event) {
                        if(event.xAxis !== undefined) {
                            let min = event.xAxis[0].min;
                            let minIndex = null;
                            let max = event.xAxis[0].max;
                            let maxIndex = null;

                            Highcharts.each(this.series[0].xData, function(p, i) {
                                if(minIndex == null && p >= min) {
                                    minIndex = i;
                                }
                                if(minIndex != null && p <= max) {
                                    maxIndex = i;
                                }
                            });
                            //console.log(minIndex + " - " + maxIndex);
                            self.props.callRange(minIndex,  maxIndex);
                        }
                        var text, label;
                        if (event.xAxis) {
                            text = 'min: ' + Highcharts.numberFormat(event.xAxis[0].min, 2) + ', max: ' + Highcharts.numberFormat(event.xAxis[0].max, 2);
                        } else {
                            text = 'Selection reset';
                        }
                        label = this.renderer.label(text, 100, 120)
                            .attr({
                                fill: Highcharts.getOptions().colors[0],
                                padding: 10,
                                r: 5,
                                zIndex: 8
                            })
                            .css({
                                color: '#FFFFFF'
                            })
                            .add();

                        setTimeout(function () {
                            label.fadeOut();
                        }, 1000);

                    }
                },
                zoomType: 'x'
            },
            credits: { enabled: false },
            legend: { enabled: false },
            plotOptions: {
                line: { marker: { enabled: false } },
                column: { pointWidth: 6 }
            },
            series: [{
                data: speed,
                name: "Speed",
                type: "line",
                color: Highcharts.getOptions().colors[0],
                fillOpacity: 0.3,
                tooltip: {
                    valueSuffix: ' km/h'
                }
            }],
                title: {
                text: "Speed",
                    align: 'left',
                    margin: 0,
                    x: 30
            },
            tooltip: {
                positioner: function () {
                    return {
                        // right aligned
                        x: this.chart.chartWidth - this.label.width,
                        y: 10 // align to title
                    };
                },
                borderWidth: 0,
                    backgroundColor: 'none',
                    pointFormat: '{point.y}',
                    headerFormat: '',
                    shadow: false,
                    style: {
                    fontSize: '18px'
                },
                valueDecimals: speed.valueDecimals
            },
            xAxis: {
                min: 0,
                    crosshair: true,
                    events: {
                        setExtremes: function(e) {
                            Highcharts.syncExtremes(e);
                        }
                    },
                labels: {
                    format: '{value} km'
                }
            },
            yAxis: {
                tickPositions: [speedrange.min, speedrange.max],
                    startOnTick: false,
                    min: speedrange.min,
                    max: speedrange.max,
                    title: {
                    text: null
                }
            }
        }

        const optionsHr = {
            chart: {
                marginLeft: 40, // Keep all charts left aligned
                    spacingTop: 20,
                    spacingBottom: 20
            },
            credits: { enabled: false },
            legend: { enabled: false },
            plotOptions: {
                line: { marker: { enabled: false } },
                column: { pointWidth: 6 }
            },
            series: [{
                data: heartrate,
                name: "Heart rate",
                type: "area",
                color: Highcharts.getOptions().colors[2],
                fillOpacity: 0.3,
                tooltip: {
                    valueSuffix: ' bpm'
                }
            }],
                title: {
                text: "Heart rate",
                    align: 'left',
                    margin: 0,
                    x: 30
            },
            tooltip: {
                positioner: function () {
                    return {
                        // right aligned
                        x: this.chart.chartWidth - this.label.width,
                        y: 10 // align to title
                    };
                },
                borderWidth: 0,
                    backgroundColor: 'none',
                    pointFormat: '{point.y}',
                    headerFormat: '',
                    shadow: false,
                    style: {
                    fontSize: '18px'
                },
                valueDecimals: heartrate.valueDecimals
            },
            xAxis: {
                min: 0,
                    crosshair: true,
                    events: {
                    setExtremes: function(e) {
                        Highcharts.syncExtremes(e);
                    }
                },
                labels: {
                    format: '{value} km'
                }
            },
            yAxis: {
                tickPositions: [hrrange.min, hrrange.max],
                    startOnTick: false,
                    min: hrrange.min,
                    max: hrrange.max,
                    title: {
                    text: null
                }
            }
        }

        return(
            <div id="container">
                <HighchartsReact highcharts={Highcharts} options={optionsSpeed} containerProps={{ style: { height: "150px" } }}/>
                <HighchartsReact highcharts={Highcharts} options={optionsAlt} containerProps={{ style: { height: "150px" } }}/>
                <HighchartsReact highcharts={Highcharts} options={optionsHr} containerProps={{ style: { height: "150px" } }}/>
            </div>
        );
    }
};

export default Charts;