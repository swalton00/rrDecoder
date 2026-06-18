This project will read one or more JMRI DecoderPro roster.xml files and store in contents in a relational database.
Once imported, you then import the detailed contents of the roster files. This will allow you to view serveral
of the characteristics of the roster, and compare values between decoders. The viewable elements include:
CV's, speed profiles, key values, and Function Key labels. You can print the various screens. The date
the roster was updated by DecoderPro and the date that it was imported are shown, you can see wether your
view of the decoder data is current.

Requires Java 11 or later (the same requirements as JMRI). No relational database experience is needed. The
relational database is included with the distribution.

No installation routine is provided. Simply unzip (or, for UNIX or Mac, untar) the appropriate file. Then
from the command line change to the decoderDB/bin directory and run the "app" or "app.bat" program. Or, double-click
on the "app" or "app.bat" icon in FIle Manager. The first time you run the program it will require you to 
enter some parameters to allow it to create the database. The values will be saved and after that the program
will simply show the main screen (unless something happens to the database).
