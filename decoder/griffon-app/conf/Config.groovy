application {
    title = 'decoder'
    startupGroups = ['decoder']
    autoShutdown = true
}
windowManager {
   defaultHandler = new com.spw.rr.CenteringWindowDisplayHandler()
}
mvcGroups {
    // MVC Group for "decoder"
    'decoder' {
        model      = 'com.spw.rr.DecoderModel'
        view       = 'com.spw.rr.DecoderView'
        controller = 'com.spw.rr.DecoderController'
    }
}