package com.spw.rr.utilities

import java.awt.Component
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FrameHelper implements ComponentListener{
    private static final Logger log = LoggerFactory.getLogger(FrameHelper.class)
    private static final PropertySaver saver = PropertySaver.getInstance()
    public static final String X_NAME = "X"
    public static final String Y_NAME = "Y"
    public static final String WIDTH_NAME = "Width"
    public static final String HEIGHT_NAME = "Height"

    @Override
    void componentResized(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
        Dimension dim = source.getSize()
        saver.putInt(name, WIDTH_NAME, (int)dim.getWidth())
        saver.putInt(name, HEIGHT_NAME, (int)dim.getHeight())
    }

    @Override
    void componentMoved(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
        Point p = e.getComponent().getLocation()
        saver.putInt(name, X_NAME, (int) p.getX())
        saver.putInt(name, Y_NAME, (int) p.getY())
    }

    @Override
    void componentShown(ComponentEvent e) {

    }

    @Override
    void componentHidden(ComponentEvent e) {

    }
}
