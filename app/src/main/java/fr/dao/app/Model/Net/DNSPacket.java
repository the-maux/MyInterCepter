package fr.dao.app.Model.Net;

public class                    DNSPacket  {
    private String              TAG = getClass().getName();
    String                      request;	//Raw packet on Hex codification
    public byte[]               IP;		//IP Header
    public byte[]               UDP;		//UDP Header
    public byte[]               DNS;		//DNS Message
    public byte[]               RawQuerys;	//DNS Message Queries
    public boolean              OK=false;	//well formed DNS message?
    public int                  IP_LENGTH;		//length of IP Header
    public int                  UDP_LENGTH=16;	//set to 16
    public int                  DNS_LENGTH;		// DNS length

    public                      DNSPacket(String request){
        String temp=request;
        byte[] temp_byte=request.getBytes();

        //get IP_LENGTH (num. of 4bytes words*2char/byte*4bytes/word)
        IP_LENGTH=Integer.parseInt(temp.substring(1, 2),10)*8;

        DNS_LENGTH=temp.length()-(UDP_LENGTH+IP_LENGTH);

        //copy the IP header
        IP=new byte[IP_LENGTH];
        java.lang.System.arraycopy(temp_byte, 0, IP, 0, IP_LENGTH);
        //remove ip header from string/array request
        temp=temp.substring(IP_LENGTH);
        temp_byte=temp.getBytes();

        //copy the udp header
        UDP=new byte[UDP_LENGTH];
        java.lang.System.arraycopy(temp_byte, 0, UDP, 0, UDP_LENGTH);
        //remove udp header from string/array request
        temp=temp.substring(UDP_LENGTH);
        temp_byte=temp.getBytes();

        //copy the dns header
        DNS=new byte[DNS_LENGTH];
        java.lang.System.arraycopy(temp_byte, 0, DNS, 0, DNS_LENGTH);
        //remove udp header from string/array request
        temp=temp.substring(DNS_LENGTH);
        temp_byte=temp.getBytes();

        if((DNS_LENGTH>32)){
            //well formed query
            OK=true;
            //get the queries contained on the message
            RawQuerys=new String(DNS).substring(24,DNS_LENGTH).getBytes();

        }

    }

    //returs query's TransactionID
    public String               getTransacionID(){

        String DNS=new String(this.DNS);
        String Tid=(DNS.substring(0,4));

        return Tid;
    }

    //returns query's flags
    public String               getQueryFlags(){
        String DNS=new String(this.DNS);

        return DNS.substring(4,8);
    }
    //returns number of queries contained on DNS message
    public int                  getNumberOfQueries(){
        String DNS=new String(this.DNS);
        int number=Integer.parseInt(DNS.substring(8,12),16);

        return number;
    }

    public int                  getNumberOfAuthRR(){
        String DNS=new String(this.DNS);
        int number=Integer.parseInt(DNS.substring(16,20),16);

        return number;
    }

    public int                  getNumberOfAdditionalRR(){
        String DNS=new String(this.DNS);
        int number=Integer.parseInt(DNS.substring(20,24),16);

        return number;
    }

    public byte[]               getRawQuerys(){
        return this.RawQuerys;
    }

    //returns DNS Message src port
    public int                  getSRCPort(){
        String UDP=new String(this.UDP);
        int port=Integer.parseInt(UDP.substring(0,4),16);

        return port;
    }

    //returns DNS Message src IP
    public String               getSRCIp(){
        String IP=new String(this.IP).substring(24,32);
        String b1=String.valueOf(Integer.parseInt(IP.substring(0,2),16));
        String b2=String.valueOf(Integer.parseInt(IP.substring(2,4),16));
        String b3=String.valueOf(Integer.parseInt(IP.substring(4,6),16));
        String b4=String.valueOf(Integer.parseInt(IP.substring(6,8),16));


        return b1+"."+b2+"."+b3+"."+b4;

    }
    //returns DNS Message DNS IP
    public String               getDSTIp(){
        String IP=new String(this.IP).substring(32,this.IP.length);
        String b1=String.valueOf(Integer.parseInt(IP.substring(0,2),16));
        String b2=String.valueOf(Integer.parseInt(IP.substring(2,4),16));
        String b3=String.valueOf(Integer.parseInt(IP.substring(4,6),16));
        String b4=String.valueOf(Integer.parseInt(IP.substring(6,8),16));


        return b1+"."+b2+"."+b3+"."+b4;

    }

    //returns DNS Message dst port
    public int                  getDSTPort(){
        return 53;
    }

    //return the queried Host
    public String               getQueriedHost(){
        byte[] array=hexStringToByteArray(new String(this.RawQuerys).substring(2, RawQuerys.length-10));
        return new String(array);
    }

    //return type of query
    public int                  getQuerytype(){
        return Integer.parseInt(new String(RawQuerys).substring(RawQuerys.length-8,RawQuerys.length-4),16);

    }
    //return query's class
    public int                  getQueryClass(){
        return Integer.parseInt(new String(RawQuerys).substring(RawQuerys.length-4,RawQuerys.length),16);

    }

    public static byte[]        hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
