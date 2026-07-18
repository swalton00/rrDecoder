package com.spw.rr.utilities

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class ApplyResources {
    private static final Logger log = LoggerFactory.getLogger(ApplyResources.class)

    static boolean endOfStream = false


    /**
     * Read a resource file and apply all statements contained to the database connection supplied
     * @param resName the name of the resource
     * @param conn the connection to the database
     * @return true if the creation is successful, false otherwise
     */
    boolean apply(String resName, Connection conn) {
        log.debug("applying resources from ${resName}")
        InputStream is = this.class.getClassLoader().getResourceAsStream(resName)
        InputStreamReader isRdr = new InputStreamReader(is)
        BufferedReader rdr = new BufferedReader(isRdr)
        endOfStream = false
        // Initialize this once before your extraction loop
        def sqlState = [
                inSingleQuotes: false,
                inDoubleQuotes: false,
                inLineComment : false,
                inBlockComment: false,
                isEscaped     : false,
                lookaheadChar : -2 // -2 means uninitialized
        ]
        try {
            while (!endOfStream) {
                String stmt = getNextStatement(rdr, sqlState)
                if (!stmt) {
                    log.debug("reached the end")
                    break
                }
                log.debug("executing: {}", stmt)
                PreparedStatement preped = conn.prepareStatement(stmt)
                preped.execute()
            }
        } catch (SQLException sqle) {
            log.error("SQL exeception executing statement - ", sqle)
            return false
        } catch (Exception e) {
            log.error("Exeception processing", e)
            return false
        } finally {
            if (rdr) {
                rdr.close()
            }
        }
        return true
    }

    /**
     * Given a Buffered reader and a current state, returns the next SQL statement
     * @param rdr BufferedReader opened to the resource stream
     * @param state current state to retain the current state
     * @return the next SQL statement
     * @throws IOException
     */
    private String getNextStatement(BufferedReader reader, Map state) throws IOException {
        StringBuilder sb = new StringBuilder()

        // Initialize lookahead character on the very first call
        if (state.lookaheadChar == -2) {
            state.lookaheadChar = reader.read()
        }

        while (state.lookaheadChar != -1) {
            int currCode = state.lookaheadChar
            state.lookaheadChar = reader.read() // Advance lookahead

            char ch = (char) currCode
            char nextCh = state.lookaheadChar != -1 ? (char) state.lookaheadChar : (char) 0

            // 1. Handle existing comment blocks
            if (state.inLineComment) {
                if (ch == '\n' || ch == '\r') {
                    state.inLineComment = false
                }
                continue
            }
            if (state.inBlockComment) {
                if (ch == '*' && nextCh == '/') {
                    state.inBlockComment = false
                    state.lookaheadChar = reader.read() // Consume the '/'
                }
                continue
            }

            // 2. Detect entering comment blocks
            if (!state.inSingleQuotes && !state.inDoubleQuotes) {
                if ((ch == '-' && nextCh == '-') || ch == '#') {
                    state.inLineComment = true
                    if (ch == '-') {
                        state.lookaheadChar = reader.read()
                    } // Consume second '-'
                    continue
                }
                if (ch == '/' && nextCh == '*') {
                    state.inBlockComment = true
                    state.lookaheadChar = reader.read() // Consume '*'
                    continue
                }
            }

            // 3. Append valid SQL character
            sb.append(ch)

            // 4. Escape character logic
            if (ch == '\\' && !state.isEscaped) {
                state.isEscaped = true
                continue
            }

            // 5. String literal tracking
            if (ch == '\'' && !state.inDoubleQuotes && !state.isEscaped) {
                state.inSingleQuotes = !state.inSingleQuotes
            } else if (ch == '"' && !state.inSingleQuotes && !state.isEscaped) {
                state.inDoubleQuotes = !state.inDoubleQuotes
            }
            // 6. Delimiter Detection -> Return instantly to the caller
            else if (ch == ';' && !state.inSingleQuotes && !state.inDoubleQuotes) {
                // Reset escape flag for safety
                state.isEscaped = false

                String stmt = sb.toString().trim()
                if (!stmt.isEmpty()) {
                    return stmt
                }
                // If it was an empty statement (like duplicated clean semicolons ";;"), keep looking
            }

            // Reset escape flag if it was active
            if (state.isEscaped && ch != '\\') {
                state.isEscaped = false
            }
        }

        // EOF reached: Return any final leftover text
        String finalStmt = sb.toString().trim()
        return finalStmt.isEmpty() ? null : finalStmt
    }
}
