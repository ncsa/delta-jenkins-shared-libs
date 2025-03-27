def createBatchFile(params) {
    def reservationOption = params.RESERVATION ? "#SBATCH --reservation=${params.RESERVATION}" : ""
    def nodeListOption = params.NODELIST ? "#SBATCH --nodelist=${params.NODELIST}" : ""
    def commands = params.COMMANDS ? params.COMMANDS.join('\n') : ""

    return """
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
"""
}