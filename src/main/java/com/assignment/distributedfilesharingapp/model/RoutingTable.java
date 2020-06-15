package com.assignment.distributedfilesharingapp.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RoutingTable {

    @Value("${app.node.max-neighbours}")
    private Integer maxNeighbours;

    @Value("${app.node.min-neighbours}")
    private Integer minNeighbours;

    @Getter
    private List<Neighbour> neighbours = new ArrayList<>();
    @Getter
    private final String address;
    @Getter
    private final int port;

    public RoutingTable(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public synchronized Integer addNeighbour(String address, Integer port, Integer clientPort) {
        if (!neighbours.isEmpty()) {
            return (int) neighbours.stream().map(neighbour -> {
                if (neighbour.getAddress().equals(address) && neighbour.getPort().equals(port)) {
                    neighbour.Ping();
                    return neighbours.size();
                } else {
                    return 0;
                }
            }).count();
        }
        if (neighbours.size() >= maxNeighbours) {
            log.info("Cannot add neighbour : " + address + ":" + port);
            return 0;
        }
        neighbours.add(new Neighbour(address, port, clientPort));
        log.info("Adding neighbour : " + address + ":" + port);
        return neighbours.size();
    }

    public synchronized Integer removeNeighbour(String address, Integer port) {
        if (!neighbours.isEmpty()) {
            return (int) neighbours.stream().map(neighbour -> {
                if (neighbour.getAddress().equals(address) && neighbour.getPort().equals(port)) {
                    log.info("remove neighbour address {} and port is {}", address, port);
                    neighbours.remove(neighbour);
                }
                return neighbours.size();
            }).count();
        }
        return 0;
    }

    public synchronized Integer getNeighboursCount() {
        return neighbours.size();
    }

    public synchronized void printRoutingTable() {
        log.info("\n\n--------------------------------");
        log.info("Total neighbours: {}", neighbours.size());
        log.info("Address: {}:{}", address, port);
        log.info("--------------------------------");
        neighbours.forEach(neighbour -> log.info("Address: {} Port: {} Ping: {}", neighbour.getAddress(), neighbour.getPort(), neighbour.getPingPongs()));
        log.info("--------------------------------\n\n");
    }

    public synchronized List<String> getStringNeighbourList() {
        return neighbours
                .stream()
                .map(Neighbour::toString)
                .collect(Collectors.toList());
    }

    public boolean isANeighbour(String address, int port) {
        return neighbours
                .stream()
                .map(neighbour -> neighbour.getAddress().equals(address) && neighbour.getPort().equals(port))
                .findFirst()
                .orElse(false);
    }

    public List<String> getOtherNeighbours(String address, int port) {
        return neighbours.stream()
                .filter(neighbour -> !(neighbour.getAddress().equals(address) && neighbour.getPort().equals(port)))
                .map(Neighbour::toString).collect(Collectors.toList());
    }
}