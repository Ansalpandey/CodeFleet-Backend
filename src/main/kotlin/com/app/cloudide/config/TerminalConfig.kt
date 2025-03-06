package com.app.cloudide.config

import com.pty4j.PtyProcess
import com.pty4j.PtyProcessBuilder
import java.io.IOException

class TerminalSession(
    private val process: PtyProcess,
    private val outputHandler: (String) -> Unit
) {
    private val inputWriter = process.outputStream.writer()
    private val outputReader = process.inputStream.bufferedReader()

    init {
        startOutputReaderThread()
    }

    private fun startOutputReaderThread() {
        Thread {
            try {
                val buffer = CharArray(1024)
                while (process.isRunning) {
                    val read = outputReader.read(buffer)
                    if (read > 0) {
                        outputHandler(String(buffer, 0, read))
                    }
                }
            } catch (e: Exception) {
                outputHandler("\r\n*** Terminal session closed ***\r\n")
            } finally {
                destroy()
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    fun write(input: String) {
        try {
            inputWriter.write(input)
            inputWriter.flush()
        } catch (e: IOException) {
            destroy()
        }
    }

    fun destroy() {
        try {
            process.destroy()
            inputWriter.close()
            outputReader.close()
        } catch (e: Exception) {
            // Handle cleanup errors
        }
    }
}

object TerminalFactory {
    fun createDockerTerminal(
        containerId: String,
        outputHandler: (String) -> Unit,
        cols: Int = 80,
        rows: Int = 30
    ): TerminalSession {
        val cmd = arrayOf(
            "docker", "exec", "-it",
            containerId, "sh", "-c", "stty -echo; TERM=xterm-256color; export TERM; exec bash"
        )

        val process = PtyProcessBuilder()
            .setCommand(cmd)
            .setEnvironment(System.getenv())
            .setInitialColumns(cols)
            .setInitialRows(rows)
            .start()

        return TerminalSession(process, outputHandler)
    }
}