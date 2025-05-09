package id.ac.ui.cs.advprog.log.service.command;

import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;

public class RejectLogCommand implements LogCommand {
    private final Log log;

    public RejectLogCommand(Log log) {
        this.log = log;
    }

    @Override
    public void execute() {
        log.setStatus(StatusLog.DITOLAK);
    }
}