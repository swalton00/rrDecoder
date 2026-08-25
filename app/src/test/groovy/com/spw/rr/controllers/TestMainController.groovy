package com.spw.rr.controllers

import com.spw.rr.models.MainModel
import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import com.spw.rr.views.MainView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path

import org.apache.ibatis.session.SqlSession

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

class TestMainController {

    private static final String H2_URL = "jdbc:h2:mem:mainController;DB_CLOSE_DELAY=-1"
    private static final String H2_USER = "test"
    private static final String H2_PASSWORD = "testpw"
    private static final String H2_SCHEMA = "test"

    private final PropertySaver saver = PropertySaver.getInstance()
    private Path temporaryHome
    private String originalUserHome

    @BeforeEach
    void setUp() {
        originalUserHome = System.getProperty("user.home")
        temporaryHome = Files.createTempDirectory("main-controller-test")
        System.setProperty("user.home", temporaryHome.toString())
        resetPropertySaver()
    }

    @AfterEach
    void tearDown() {
        resetPropertySaver()
        System.setProperty("user.home", originalUserHome)
        temporaryHome.toFile().deleteDir()
    }

    @Test
    void initializesWithoutPropertiesByRequestingNewDatabaseSettings() {
        TestController controller = newController()

        controller.init()

        assertNull(controller.settings.url)
        assertFalse(controller.settings.settingsComplete)
        assertFalse(controller.settings.settingsValid)
        assertFalse(controller.settings.databaseOpen)
        assertTrue(controller.settingsDialogRequested)
    }

    @Test
    void initializesAndOpensExistingDatabaseWhenPropertiesArePresent() {
        writeProperties()
        TestController controller = newController()

        controller.init()

        assertTrue(controller.settings.settingsComplete)
        assertTrue(controller.settings.settingsValid)
        assertTrue(controller.settings.databaseOpen)
        assertTrue(controller.initializationCompleted)
        assertFalse(controller.settingsDialogRequested)
    }

    @Test
    void opensNewDatabaseAndDisplaysEmptyViewAfterSettingsAreEntered() {
        TestController controller = newController()
        controller.createProps = { -> controller.enterNewDatabaseSettings() }

        controller.init()

        assertTrue(controller.settings.settingsComplete)
        assertTrue(controller.settings.settingsValid)
        assertTrue(controller.settings.databaseOpen)
        assertTrue(controller.initializationCompleted)
        assertTrue(controller.rosterIdList.isEmpty())
        assertTrue(controller.view.tableModel.getRowCount() == 0)
    }

    @Test
    void importsRosterIntoEmptyDatabaseAndDisplaysFourEntries() {
        TestController controller = newController()
        controller.databaseUrl = "jdbc:h2:mem:mainControllerRoster;DB_CLOSE_DELAY=-1"
        controller.createProps = { -> controller.enterNewDatabaseSettings() }
        controller.init()

        File rosterFile = new File(getClass().getResource("/roster.xml").toURI())
        def importedRoster = controller.imports.importRoster(null, rosterFile)
        controller.completeInit()

        assertNotNull(importedRoster)
        assertEquals(4, importedRoster.decCount)
        assertEquals(1, controller.rosterIdList.size())
        assertEquals(1, controller.view.tableModel.getRowCount())
    }

    @Test
    void importsTheSameRosterAgainIntoAnExistingDatabase() {
        TestController controller = newController()
        controller.databaseUrl = "jdbc:h2:mem:mainControllerReimport;DB_CLOSE_DELAY=-1"
        controller.createProps = { -> controller.enterNewDatabaseSettings() }
        controller.init()

        File rosterFile = new File(getClass().getResource("/roster.xml").toURI())
        def firstImport = controller.imports.importRoster(null, rosterFile)
        def secondImport = controller.imports.importRoster(null, rosterFile)

        assertNotNull(firstImport)
        assertNotNull(secondImport)
        assertEquals(4, secondImport.decCount)
        assertEquals(1, controller.databaseServices.listRosters().size())
        assertEquals(4, controller.databaseServices
                .decodersForRosterList([secondImport.id]).size())
    }

    @Test
    void importsDetailsForAllFourImportedEntries() {
        TestController controller = newController()
        controller.databaseUrl = "jdbc:h2:mem:mainControllerDetails;DB_CLOSE_DELAY=-1"
        controller.createProps = { -> controller.enterNewDatabaseSettings() }
        controller.init()

        File rosterFile = new File(getClass().getResource("/roster.xml").toURI())
        def importedRoster = controller.imports.importRoster(null, rosterFile)
        List<DecoderEntry> decoderEntries = controller.databaseServices
                .decodersForRosterList([importedRoster.id])
        ArrayList<Integer> decoderIds = decoderEntries.collect { it.id } as ArrayList<Integer>

        controller.imports.importDetailRoster(null, [importedRoster.id] as ArrayList<Integer>)

        assertEquals(4, decoderIds.size())
        int labelCount = 0
        int keyValueCount = 0
        decoderIds.each { Integer decoderId ->
            SqlSession session = controller.databaseServices.sqlSessionFactory.openSession(true)
            try {
                def mapper = session.getMapper(com.spw.rr.database.Mapper)
                assertFalse(mapper.getCvValuesFor(decoderId).isEmpty())
                labelCount += mapper.getFunctionLabels(decoderId).size()
                keyValueCount += mapper.getKeyValuesFor(decoderId).size()
            } finally {
                session.close()
            }
        }
        assertEquals(8, labelCount)
        assertEquals(1, keyValueCount)
    }

    private TestController newController() {
        TestController controller = new TestController()
        controller.databaseServices = DatabaseServices.getInstance()
        return controller
    }

    private void writeProperties() {
        saver.init()
        saver.putBaseString("URL", H2_URL)
        saver.putBaseString("Userid", H2_USER)
        saver.putBaseString("pw", H2_PASSWORD)
        saver.putBaseString("Schema", H2_SCHEMA)
        saver.writeValues()
        resetPropertySaver()
    }

    private void resetPropertySaver() {
        saver.properties.clear()
        saver.inited = false
        saver.dirty = false
    }

    private static class TestController extends MainController {
        boolean settingsDialogRequested
        boolean initializationCompleted
        String databaseUrl = H2_URL

        @Override
        protected MainView createView(MainModel mainModel) {
            TestView testView = new TestView()
            testView.model = mainModel
            return testView
        }

        @Override
        void completeInit() {
            super.completeInit()
            initializationCompleted = true
        }

        void enterNewDatabaseSettings() {
            settings.url = databaseUrl
            settings.userid = H2_USER
            settings.password = H2_PASSWORD
            settings.schema = H2_SCHEMA
            settings.settingsComplete = true
            settings.settingsValid = databaseServices.validate(settings)
            if (settings.settingsValid) {
                databaseServices.dbStart(settings)
                completeInit()
            }
        }

        TestController() {
            createProps = { -> settingsDialogRequested = true }
        }
    }

    private static class TestView extends MainView {
        @Override
        void init() {
            tableModel = new RrTableModel(model)
        }
    }
}
