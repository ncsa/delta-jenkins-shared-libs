def createRemoteBatchFile(String remoteUser, String remoteHost, String workDir, Map params) {
    def reservationOption = params.RESERVATION ? "#SBATCH --reservation=${params.RESERVATION}" : ""
    def nodeListOption = params.NODELIST ? "#SBATCH --nodelist=${params.NODELIST}" : ""
    def commands = params.COMMANDS ? params.COMMANDS.join('\n') : ""

    def remoteScript = """
    mkdir -p "${workDir}"
    cd "${workDir}"

    echo "[Debug] Checking out the pennylane-lightning repository"
    git clone https://github.com/PennyLaneAI/pennylane-lightning.git ./pennylane-lightning
    cd pennylane-lightning
    git fetch --all --tags
    git checkout v${params.PENNYLANE_VERSION}
    cd ..

    cat <<BATCH > job.sbatch
#!/bin/bash
#SBATCH --account=bbka-dtai-gh
#SBATCH --partition=${params.PARTITION}
#SBATCH --nodes=1
#SBATCH --mem=16GB
#SBATCH --ntasks-per-node=1
#SBATCH --gpus-per-node=1
#SBATCH --cpus-per-task=16
#SBATCH --time=01:00:00
#SBATCH --output=job_output.log
${reservationOption}
${nodeListOption}

${commands}
BATCH

    echo "[Debug] Done creating job.sbatch"
    """

    def sshCommand = "ssh -o StrictHostKeyChecking=no -T ${remoteUser}@${remoteHost} 'bash -s' <<'EOF'\n${remoteScript}\nEOF"
    sh(script: sshCommand)
}