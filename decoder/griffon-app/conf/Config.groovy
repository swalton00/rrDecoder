application {
    title = 'RR Decoder Database'
    startupGroups = ['decoder', 'prefs', 'dec', 'progress']
    autoShutdown = true
}
windowManager {
    defaultHandler = new com.spw.rr.CenteringWindowDisplayHandler()
    startingWindow = 'mainWindow'
}
mvcGroups {
    // MVC Group for "app"
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
    'dec'  {
        model = 'com.spw.rr.DecModel'
        view  = 'com.spw.rr.DecView'
        controller = 'com.spw.rr.DecController'
    }
    'progress' {
        model = 'com.spw.rr.ProgressModel'
        view  = 'com.spw.rr.ProgressView'
        controller = 'com.spw.rr.ProgressController'
    }

}