package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.entity.Serie;

public class SeriesLesPlusLonguesDTO {
    Serie serieEnCoursLaPlusLongue;
    Serie serieTermineeLaPlusLongue;

    public SeriesLesPlusLonguesDTO(Serie serieEnCoursLaPlusLongue, Serie  serieTermineeLaPlusLongue) {
        this.serieEnCoursLaPlusLongue = serieEnCoursLaPlusLongue;
        this.serieTermineeLaPlusLongue = serieTermineeLaPlusLongue;
    }

    public Serie getSerieEnCoursLaPlusLongue() {
        return serieEnCoursLaPlusLongue;
    }

    public Serie getSerieTermineeLaPlusLongue() {
        return serieTermineeLaPlusLongue;
    }
}
