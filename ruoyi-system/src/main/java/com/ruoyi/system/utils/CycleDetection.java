package com.ruoyi.system.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.*;

@Data
class Edge {
    int fromId;
    int toId;

    public Edge(int fromId, int toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                '}';
    }
}


// 并查集算法
class UnionFind {
    private int[] parent;

    public UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootX] = rootY;
        }
    }

    public boolean isConnected(int x, int y) {
        return find(x) == find(y);
    }
}


public class CycleDetection {

    public static boolean hasCycle(List<Edge> edges, int numVertices) {
        UnionFind uf = new UnionFind(numVertices);

        for (Edge edge : edges) {
            int fromId = edge.getFromId();
            int toId = edge.getToId();

            // 如果fromId和toId在同一个集合中，那么存在环路
            if (uf.isConnected(fromId, toId)) {
                return true;
            }

            // 否则，合并这两个集合
            uf.union(fromId, toId);
        }

        // 如果没有找到环路，返回false
        return false;
    }

    // 节点id集合
    public static Set<Integer> nodeIdSet = new HashSet<>();

    // 全部环路
    public static List<List<Integer>> allPath = new ArrayList<>();
    // 环路去重
    public static HashSet<List<Integer>> allPathSet = new HashSet<>();

    // 路径
    public static Deque<Integer> path = new LinkedList<>();

    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 0));
        edges.add(new Edge(1, 2));
        edges.add(new Edge(2, 1));
        edges.add(new Edge(1, 3));
        edges.add(new Edge(3, 4));
        edges.add(new Edge(4, 5));
        edges.add(new Edge(5, 3));


        Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
        Map<Integer,Integer> nodeIndex = new HashMap<>();

        for (Edge edge : edges) {
            nodeIdSet.add(edge.getFromId());
            nodeIdSet.add(edge.getToId());

            adjacencyList.computeIfAbsent(edge.fromId, k -> new ArrayList<>()).add(edge);

        }

        System.out.println(nodeIdSet);
        List<Integer> nodeIdList = new ArrayList<>(nodeIdSet);
        // nodeId : index 的键值对


        for (int i = 0; i < nodeIdList.size(); i++) {
            nodeIndex.put(nodeIdList.get(i),i);
        }

        findCyclePath(nodeIdList,edges,nodeIndex,adjacencyList);


        System.out.println(allPath);
        allPathSet = new HashSet<>(allPath);
        System.out.println(allPathSet);

    }


    public static void findCyclePath(List<Integer> nodeIdList,List<Edge> edges, Map<Integer,Integer> nodeIndex,Map<Integer,List<Edge>> adjacencyList){
        boolean[] visited = new boolean[nodeIdList.size()];

        for (Edge edge : edges) {
            int fromId = edge.getFromId();
            // 节点没有访问
            if(!visited[nodeIndex.get(fromId)]){
                dfsUtil(nodeIdList,edges,nodeIndex,visited,fromId,adjacencyList);
            }
        }
    }

    // nodeId为当前访问的节点
    public static void dfsUtil(List<Integer> nodeIdList,List<Edge> edges,Map<Integer,Integer> nodeIndex,boolean[] visited,int nodeId,Map<Integer, List<Edge>> adjacencyList){
        // 标记访问
        visited[nodeIndex.get(nodeId)] = true;
        // 添加路径
        path.addLast(nodeId);

        // 获得该节点的边
        List<Edge> edges1 = adjacencyList.get(nodeId);

        // 该节点没有外出边
        if(edges1 == null || edges1.size() == 0){
            // 结束
            // 取消访问标记
            visited[nodeIndex.get(nodeId)] = false;
            // 路径中删除该节点
            path.removeLast();
            return;
        }

        // 遍历这些边
        for (Edge edge : edges1) {
            // 邻节点id
            int toId = edge.getToId();
            // 如果被访问过，就说明出现了环
            if(visited[nodeIndex.get(toId)]){
                // 将当前路径添加在allPath中
                path.addLast(toId);
//                allPath.add(new LinkedList<>(path));
                handleCyclePath(new ArrayList<>(path));
                path.removeLast();
            }else{
                // 递归
                dfsUtil(nodeIdList,edges,nodeIndex,visited,toId,adjacencyList);
            }

        }

        // 该节点访问结束
        // 取消访问标记
        visited[nodeIndex.get(nodeId)] = false;
        // 路径中删除该节点
        path.removeLast();

    }

    // 处理环路路径
    public static void handleCyclePath(List<Integer> path){
        Map<Integer,Integer> map = new HashMap<>();
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < path.size(); i++) {
            if (map.containsKey(path.get(i))) {
                startIndex = map.get(path.get(i));
                endIndex = i;
                break;
            }

            map.put(path.get(i),i);
        }

        List<Integer> subList = path.subList(startIndex,endIndex);
        subList.sort((o1,o2)->o1-o2);
        allPath.add(subList);
        System.out.println(subList);
    }
}
