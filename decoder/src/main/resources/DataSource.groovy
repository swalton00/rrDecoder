dataSource {
    driverClassName = 'org.h2.Driver'
    username = 'sa'
    password = ''
    pool {
        idleTimeout = 60000
        maximumPoolSize = 8
        minimumIdle = 5
    }
}

environments {
    development {
        dataSource {
            driverClassName = 'org.h2.Driver'
            username = 'rr'
            password = 'rrpass'
            dbCreate = 'skip' // one of ['create', 'skip']
            url = 'jdbc:h2:file:D:/Projects/RailRoad_Database/Dev/test;AUTO_SERVER=TRUE;MODE=DB2;'
        }
    }
    test {
        dataSource {
            driverClassName = 'org.h2.Driver'
            username = 'rrdec'
            password = 'rrdecpw'
            dbCreate = 'skip'
            url = 'jdbc:h2:file:D:/Projects/RailRoad_Database/Dev/test;AUTO_SERVER=TRUE;MODE=DB2;'
        }
    }
    production {
        dataSource {
            driverClassName = 'org.h2.Driver'
            username = 'rrdec'
            password = 'rrdecpw'
            dbCreate = 'skip'
            url = 'jdbc:h2:file:D:/Projects/RailRoad_Database/Dev/test;AUTO_SERVER=TRUE;MODE=DB2;'
        }
    }
}