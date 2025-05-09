package id.ac.ui.cs.advprog.log.service.command;

import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.model.Log;

public class ApproveLogCommand implements LogCommand {
    private final Log log;

    public ApproveLogCommand(Log log) {
        this.log = log;
    }

    @Override
    public void execute() {
        log.setStatus(StatusLog.DITERIMA);
    }
}