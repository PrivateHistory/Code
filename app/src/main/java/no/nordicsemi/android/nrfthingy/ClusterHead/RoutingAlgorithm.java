package no.nordicsemi.android.nrfthingy.ClusterHead;

import java.util.HashMap;

//TODO this only works now if we dont route between cluster heads but only between Ch and sink
// there is some overhead because I just wanted to make it adaptable for clusterhead routing as well
public class RoutingAlgorithm {
    private Integer route=-1;
    private final HashMap<Integer, int[]> alreadyReceived;
    private  int RREQId;
    public static final int WAITTIME=3000;
    public static final int TIMETOLIVE=-1;
    private long receivedRREP=-1;
    private long sentRREQ=-1;
    public RoutingAlgorithm(){
        alreadyReceived=new HashMap<>();
    }
    public ClhAdvertisedData forwardRREQ(int immediateSource,  ClhAdvertisedData receivedMSG){
        receivedMSG.setImmediateSource((byte)immediateSource);
        receivedMSG.setHopCount((byte)(receivedMSG.getHopCounts()+1));
        return receivedMSG;
    }
    //For creating RREQ
    public  ClhAdvertisedData RREQ(int source, int immediateSource,  int destination){
        RREQId++;
        ClhAdvertisedData data=new ClhAdvertisedData();
        data.setDataType((byte) 1);
        data.setRoutingMsgType((byte)0);
        data.setImmediateSource((byte)source);
        data.setSourceID((byte)source);
        data.setPacketID((byte) RREQId);
        data.setDestId((byte) destination);
        return data;
    }

    public ClhAdvertisedData forwardRREP(int immediateDestination,int immediateSource, ClhAdvertisedData recievedMSG){
        recievedMSG.setImmediateSource((byte) immediateSource);
        recievedMSG.setImmediateDestination((byte)immediateDestination);
        return recievedMSG;
    }

    public  ClhAdvertisedData RREP(int destination, int immediateDestination,int source,    int packetId){
        ClhAdvertisedData data=new ClhAdvertisedData();
        data.setDataType((byte) 1);
        data.setRoutingMsgType((byte)0);
        data.setImmediateDestination((byte) immediateDestination);
        data.setDestId((byte)destination);
        data.setSourceID((byte) source);
        data.setImmediateSource((byte) source);
        data.setPacketID((byte) packetId);
        return data;
    }

    public int getRoute() {
        return route;
    }

    public Integer getPacketIdBySource(Integer sourceId) {
        if( alreadyReceived.containsKey(sourceId)){
            return alreadyReceived.get(sourceId)[0];

    }
        return -1;
    }
    //Used by sink to remember the received RREQs
    public void putAlreadyReceived(Integer sourceId,Integer packetId ){
        int[] array=new int[1];
        array[0]=packetId;
        alreadyReceived.put(sourceId, array);
    }
    //Used by
    public void putPacketAndSourceId(Integer sourceId, Integer packetId, Integer immediateSourceId){
        int[] array=new int[2];
        array[0]=packetId;
        array[1]=immediateSourceId;
        alreadyReceived.put(sourceId, array);
    }
    public int getDestToSource(Integer destination){
        if(alreadyReceived.containsKey(destination)&&alreadyReceived.get(destination).length>1){
            return alreadyReceived.get(destination)[1];
        }
        return -1;

    }
    public void setRoute(int route) {
        this.route = route;
    }

    public long getReceivedRREP() {
        return receivedRREP;
    }

    public void setReceivedRREP(long receivedRREP) {
        this.receivedRREP = receivedRREP;
    }

    public long getSentRREQ() {
        return sentRREQ;
    }

    public void setSentRREQ(long sentRREQ) {
        this.sentRREQ = sentRREQ;
    }
}
