package com.spw.rr.controllers

import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.SpeedProfile
import com.spw.rr.models.GraphModel
import com.spw.rr.viewdb.ViewDbService
import com.spw.rr.views.GraphView
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*

class GraphController {
    private static final Logger log = LoggerFactory.getLogger(GraphController.class)

    ViewDbService database = ViewDbService.getInstance()

    Vector<DecoderEntry> decoders = new Vector()
    JDialog parent

    GraphModel model

    GraphView view

    GraphController(JDialog parent, List<Integer> decoders) {
        decoders.each {
            this.decoders.add(it)
        }
        this.parent = parent
        model = new GraphModel(this)
        buildGraph()
    }


    void buildGraph() {
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.SPEED_LIST, decoders, null)
        if (decs.size() == 0) {
            log.info("returned list of decoders is empty (no speed profiles?)")
            return
        }
        log.debug("returned list has ${decs.size()} entries")
        List<String> locoList = new ArrayList<>()
        model.dataset = new XYSeriesCollection()
        decs.each {
            String thisLoco
            if (it.roadName == null | it.roadName.isBlank() | it.roadNumber == null) {
                locoList.add(it.dccAddress)
                thisLoco = it.dccAddress
            } else {
                locoList.add(it.roadName + it.roadNumber + " Forward")
                locoList.add(it.roadName + it.roadNumber + " Reverse")
                thisLoco = it.roadName + it.roadNumber
                XYSeries thisSeriesForward = new XYSeries(thisLoco + " Forward")
                XYSeries thisSeriesReverse = new XYSeries(thisLoco + " Reverse")
                it.speedValues.each { SpeedProfile spd ->
                    Double speedStep = Double.valueOf(spd.speedStep)/1000
                    XYDataItem forward = new XYDataItem(speedStep, spd.forwardValue)
                    thisSeriesForward.add(forward)
                    XYDataItem reverse = new XYDataItem(speedStep, spd.reverseValue)
                    thisSeriesReverse.add(reverse)
                }
                model.dataset.addSeries(thisSeriesForward)
                model.dataset.addSeries(thisSeriesReverse)
            }
        }
        model.chart = ChartFactory.createXYLineChart("Speed Profiles", "Throttle Percentage", "Speed", model.dataset)
        model.chartPanel = new ChartPanel(model.chart)
        model.init()
        SwingUtilities.invokeLater {
            log.debug("creating the new view")
            view = new GraphView(parent, this, model)
            view.init()
        }
    }
}
