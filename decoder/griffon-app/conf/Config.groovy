application {
    title = 'decoder'
    startupGroups = ['decoder', 'prefs']
    autoShutdown = true
}
windowManager {
    defaultHandler = new com.spw.rr.CenteringWindowDisplayHandler()
    startupWindow = 'mainWindow'
}
mvcGroups {
    // MVC Group for "decoder"
    'decoder' {
        model      = 'com.spw.rr.DecoderModel'
        view       = 'com.spw.rr.DecoderView'
        controller = 'com.spw.rr.DecoderController'
    }
    'prefs' {
        model = 'com.spw.rr.PrefsModel'
        view  = 'com.spw.rr.PrefsView'
        controller = 'com.spw.rr.PrefsController'
    }
}

