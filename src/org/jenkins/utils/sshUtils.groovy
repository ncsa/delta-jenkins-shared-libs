// This file contains utility functions for SSH operations.

def executeRemoteCommand(String remoteUser, String remoteHost, String command) {
    def sshCommand = "ssh -o StrictHostKeyChecking=no -T ${remoteUser}@${remoteHost} '${command}'"
    return sh(script: sshCommand, returnStdout: true).trim()
}

def waitForJobCompletion(String remoteUser, String remoteHost, String jobId, int pollInterval = 30) {
    while (true) {
        def squeueCmd = "ssh -o StrictHostKeyChecking=no -T ${remoteUser}@${remoteHost} squeue -j ${jobId} --noheader"
        def squeueOut = sh(script: squeueCmd, returnStdout: true).trim()
        if (squeueOut == "") {
            echo "[Debug] Job ${jobId} is no longer in the queue. Possibly completed or failed."
            break
        } else {
            echo "[Debug] Job ${jobId} is still running. Waiting..."
        }
        sleep pollInterval
    }
}

return [
    executeRemoteCommand: executeRemoteCommand
]