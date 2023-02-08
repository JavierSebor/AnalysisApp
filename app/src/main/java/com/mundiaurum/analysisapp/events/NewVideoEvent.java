package com.mundiaurum.analysisapp.events;

import com.mundiaurum.analysisapp.domain.AnalysisFile;

public class NewVideoEvent {

    private final AnalysisFile analysisFile;

    public NewVideoEvent(AnalysisFile af){ this.analysisFile = af; }

    public AnalysisFile getAnalysisFile() { return analysisFile; }
}
