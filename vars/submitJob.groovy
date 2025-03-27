def submitJob(String remoteUser, String remoteHost, String workDir, String jobFile, List<String> preCommands = []) {
    def remoteScript = """
    set -e
    ${preCommands.collect { "${it}" }.join('\n')}
    cd "${workDir}"
    echo "[Debug] Submitting job..."
    JOB_SUBMISSION=\$(sbatch ${jobFile})
    echo "\$JOB_SUBMISSION"
    """

    def submissionOutput = executeRemoteCommand(remoteUser, remoteHost, remoteScript)
    echo "[Debug] Full sbatch output:\n${submissionOutput}"

    // Extract job ID from submission output
    def tokens = submissionOutput.tokenize()
    def jobId = tokens.last()
    echo "[Debug] Parsed Job ID: ${jobId}"

    return jobId
}

// Ensure executeRemoteCommand is reusable for dynamic scripts.
def executeRemoteCommand(String remoteUser, String remoteHost, String command) {
    def sshCommand = "ssh -o StrictHostKeyChecking=no -T ${remoteUser}@${remoteHost} 'bash -s' <<'EOF'\n${command}\nEOF"
    return sh(script: sshCommand, returnStdout: true).trim()
}