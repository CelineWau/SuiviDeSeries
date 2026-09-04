package fr.celine.suivideseries.dto;

public class SeriesTermineesPlusLonguePlusCourteDTO {
    private SerieDureeLectureDTO serieTermineePlusLongue;
    private SerieDureeLectureDTO serieTermineePlusCourte;

    public SeriesTermineesPlusLonguePlusCourteDTO(SerieDureeLectureDTO serieTermineePlusLongue, SerieDureeLectureDTO serieTermineePlusCourte) {
        this.serieTermineePlusLongue = serieTermineePlusLongue;
        this.serieTermineePlusCourte = serieTermineePlusCourte;
    }

    public SerieDureeLectureDTO getSerieTermineePlusLongue() {
        return serieTermineePlusLongue;
    }

    public SerieDureeLectureDTO getSerieTermineePlusCourte() {
        return serieTermineePlusCourte;
    }
}