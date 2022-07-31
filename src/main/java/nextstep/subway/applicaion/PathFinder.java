package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathFinder {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public Path solve(Station sourceStation, Station targetStation) {
        var path = new DijkstraShortestPath(buildGraph()).getPath(sourceStation, targetStation);

        List<Station> stations = path.getVertexList();
        var distance = (int) path.getWeight();

        return new Path(stations, distance);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph() {
        var graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        getAllStations().forEach(graph::addVertex);
        getAllSections().forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance())
        );

        return graph;
    }

    private List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    private List<Section> getAllSections() {
        return lineRepository.findAll()
                .stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
