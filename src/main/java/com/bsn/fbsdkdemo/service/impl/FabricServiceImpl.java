package com.bsn.fbsdkdemo.service.impl;

import com.bsn.fbsdkdemo.bean.FabricUserBean;
import com.bsn.fbsdkdemo.service.FabricService;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kuan
 * Created on 2020/5/20.
 * @description
 */
@Slf4j
@Service
public class FabricServiceImpl implements FabricService {

    //region application.properties

    @Value("${userName}")
    private String userName;

    @Value("${mspId}")
    private String mspId;

    @Value("${keyPath}")
    private String keyPath;

    @Value("${crtPath}")
    private String crtPath;

    @Value("${peerTlsPath}")
    private String peerTlsPath;

    @Value("${peerName}")
    private String peerName;

    @Value("${peerAddr}")
    private String peerAddr;

    @Value("${ordererTlsPath}")
    private String ordererTlsPath;

    @Value("${ordererName}")
    private String ordererName;

    @Value("${ordererAddr}")
    private String ordererAddr;

    //endregion

    @Override
    //peer channel create -o orderer地址 -c 通道名称 -f 通道配置文件 --tls true --cafile orderer的tls证书
    public String createChannel(String channelName, String txFile) throws IOException, InvalidArgumentException, TransactionException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException {
        //1. 初始化客户端
        HFClient hfClient = initializeClient();
        //2. 获取通道配置信息
        ChannelConfiguration configuration = new ChannelConfiguration(Base64.getDecoder().decode(txFile.getBytes()));
        //3. 获取签名
        byte[] signData = hfClient.getChannelConfigurationSignature(configuration, hfClient.getUserContext());
        //4. 获取orderer相关信息
        Orderer orderer = initializeOrderer(hfClient,ordererTlsPath,ordererName,ordererAddr);
        //5. 创建通道、实例化通道
        Channel channel = hfClient.newChannel(channelName, orderer, configuration, signData);
        channel.initialize();
        //6. 保存序列化后的通道信息  将生成的channel文件保存到本地进行备份，防止丢失
        channel.serializeChannel(new File("channelBlock/"+channelName + ".block"));
        String result = Base64.getEncoder().encodeToString(channel.serializeChannel());
        //7. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }
        return result;
    }

    @Override
    //peer channel join -b 通道创世区块文件 --tls true --cafile orderer的tls证书
    public boolean joinChannel(String channel64String, String peerName) throws ProposalException, TransactionException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, IOException {
        //1. 初始化客户端
        HFClient hfClient = initializeClient();

        //2. 反序列化通道信息
        Channel channel = hfClient.deSerializeChannel(Base64.getDecoder().decode(channel64String.getBytes()));

        //3. 实例化通道
        channel.initialize();

        //4. 初始化节点信息
        Peer peer = initializePeer(hfClient,peerTlsPath,peerName,peerAddr);
        //5. 将节点加入通道
        channel = channel.joinPeer(peer);
        boolean result = channel != null;

        //6. 关闭通道
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown(true);
        }
        return result;
    }

    @Override
    //peer chaincode install -n 链码名称 -v 链码版本 -p 链码路径 -l 链码语言
    public Collection<ProposalResponse> installChainCode(
            String chainCodeName, String chainCodeVersion, String chainCodePath,
            String projectName, TransactionRequest.Type language) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 实例化通道
        ArrayList<Peer> peerArrayList = new ArrayList<>();
        Peer peer = initializePeer(client,peerTlsPath,peerName,peerAddr);
        peerArrayList.add(peer);
        //3. 设置链码相关信息
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        ChaincodeID chainCode = ChaincodeID.newBuilder().setName(chainCodeName).setVersion(chainCodeVersion).build();
        installProposalRequest.setChaincodeID(chainCode);
        installProposalRequest.setChaincodeSourceLocation(Paths.get(chainCodePath).toFile());
        installProposalRequest.setChaincodePath(projectName);
        installProposalRequest.setChaincodeVersion(chainCodeVersion);
        installProposalRequest.setChaincodeLanguage(language);

        //4. 向需要安装的节点发送安装链码请求
        return client.sendInstallProposal(installProposalRequest, peerArrayList);
    }

    @Override
    //peer chaincode instantiate -o orderer地址  -C 通道名称 -n 链码名称 -v 链码版本 -c '{"Args":["init"]}'
    //--tls true --cafile orderer的tls证书
    public Collection<ProposalResponse> instantiantChainCode(
            String channelName, String chainCodeName,String chainCodePath, String chainCodeVersion,
            TransactionRequest.Type language, ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 实例化通道
        Channel channel = initializeChannel(client, channelName);
        //3. 设置链码相关信息
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(120000);
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chainCodeName)
                .setPath(chainCodePath).setVersion(chainCodeVersion).build();
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setChaincodeLanguage(language);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(args);
        //4. 向需要实例化的节点发送实例化链码请求模拟交易
        Collection<ProposalResponse> proposalResponses =
                channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());
        //5. 发送真正的实例化请求
        channel.sendTransaction(proposalResponses);
        //6. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }
        return proposalResponses;
    }

    @Override
    //peer channel getinfo -c channel202005231219
    public Long getHeight(String channelName) throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException, ProposalException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 初始化通道
        Channel channel = initializeChannel(client, channelName);
        //3. 调用获取区块信息请求
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
        Long height = blockchainInfo.getBlockchainInfo().getHeight();
        //4. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }
        log.info("getHeight height {}", height);
        return height;
    }

    @Override
    //peer chaincode list --installed
    public ArrayList<String> queryInstalledChaincodes() throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, ProposalException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 初始化节点
        Peer peer = initializePeer(client,peerTlsPath,peerName,peerAddr);
        //3. 调用获取已安装链码请求
        List<Query.ChaincodeInfo> chaincodeInfoList = client.queryInstalledChaincodes(peer);
        ArrayList<String> installedChainCodesList = new ArrayList<>();
        chaincodeInfoList.forEach(chaincodeInfo -> {
            installedChainCodesList.add(chaincodeInfo.getName());
        });
        log.info("queryInstalledChaincodes installedChainCodesList {}", installedChainCodesList);

        return installedChainCodesList;
    }

    @Override
    //peer chaincode list --instantiated -C channel202005231219
    public ArrayList<String> queryInstantiatedChaincodes(String channelName) throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, ProposalException, TransactionException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 初始化通道
        Channel channel = initializeChannel(client, channelName);
        //3. 调用获取已实例化链码请求
        List<Query.ChaincodeInfo> chaincodeInfoList = channel.queryInstantiatedChaincodes(
                channel.getPeers().iterator().next());
        ArrayList<String> instantiatedChaincodesList = new ArrayList<>();
        chaincodeInfoList.forEach(chaincodeInfo -> {
            instantiatedChaincodesList.add(chaincodeInfo.getName());
        });
        //4. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }
        log.info("queryInstantiatedChaincodes instantiatedChaincodesList {}", instantiatedChaincodesList);

        return instantiatedChaincodesList;
    }

    @Override
    //peer chaincode query -o orderer地址 -C 通道名称 -n 链码名称 -c '{"Args":["方法名称","方法参数"]}'
    public Collection<ProposalResponse> query(String channelName, String chaincodeName,
                                              String func, ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 拼装query chaincode请求参数
        QueryByChaincodeRequest request = QueryByChaincodeRequest.newInstance(client.getUserContext());
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
        request.setChaincodeID(ccid);
        request.setFcn(func);
        request.setArgs(args);
        request.setProposalWaitTime(3000);
        //3. 初始化通道
        Channel channel = initializeChannel(client, channelName);
        //4. 调用链码请求
        Collection<ProposalResponse> responses = channel.queryByChaincode(request);
        //5. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }
        log.info("query responses {}", responses);
        return responses;
    }

    @Override
    //peer chaincode invoke -o orderer地址 -C 通道名称 -n 链码名称 -c '{"Args":["方法名称","方法参数"]}'
    // --tls true --cafile orderer的tls证书
    public Collection<ProposalResponse> invoke(String channelName, String chaincodeName,
                                               String func, ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
        //1. 初始化客户端
        HFClient client = initializeClient();
        //2. 拼装chaincode请求参数
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
        request.setChaincodeID(ccid);
        request.setFcn(func);
        request.setArgs(args);
        request.setProposalWaitTime(3000);
        //3. 初始化通道
        Channel channel = initializeChannel(client, channelName);
        //4. 模拟调用链码请求
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
        //5. 发送交易到排序节点进行排序
        channel.sendTransaction(responses);
        //6. 关闭通道
        if (!channel.isShutdown()) {
            channel.shutdown(true);
        }

        log.info("invoke responses {}", responses);
        return responses;
    }


    /**
     * @return HfClient
     * @Description 创建客户端
     * @Param
     **/
    private HFClient initializeClient() throws CryptoException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //初始化客户端需要用到  用户名称、MSPID、用户公私钥
        FabricUserBean fabricUserBean = new FabricUserBean(userName, mspId, crtPath, keyPath);
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(fabricUserBean);
        return client;
    }

    /**
     * @return org.hyperledger.fabric.sdk.Peer
     * @Description 初始化peer
     * @Param [client]
     **/
    private Peer initializePeer(HFClient client,String peerTlsPath,String peerName,
                                String peerAddr)
            throws InvalidArgumentException {
        //初始化peer配置需要用到peer的 tls证书、peer名称、peer地址
        Properties peerProp = new Properties();
        peerProp.setProperty("pemFile", peerTlsPath);
        peerProp.setProperty("sslProvider", "openSSL");
        peerProp.setProperty("negotiationType", "TLS");
        peerProp.setProperty("hostnameOverride", peerName);
        peerProp.setProperty("trustServerCertificate", "true");
        //用以解决grpc通讯交易长度限制 设置为10M
        peerProp.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 10 * 1024 *1024);
        return client.newPeer(peerName, peerAddr, peerProp);
    }

    /**
     * @return org.hyperledger.fabric.sdk.Orderer
     * @Description 初始化orderer
     * @Param [client]
     **/
    private Orderer initializeOrderer(HFClient client,String ordererTlsPath,
                                      String ordererName,String ordererAddr)
            throws InvalidArgumentException {
        //初始化peer配置需要用到orderer的 tls证书、orderer名称、orderer地址
        Properties ordererProp = new Properties();
        ordererProp.setProperty("pemFile", ordererTlsPath);
        ordererProp.setProperty("sslProvider", "openSSL");
        ordererProp.setProperty("negotiationType", "TLS");
        ordererProp.setProperty("hostnameOverride", ordererName);
        ordererProp.setProperty("trustServerCertificate", "true");
        //用以解决grpc通讯交易长度限制 设置为10M
        ordererProp.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 10 * 1024 *1024);
        return client.newOrderer(ordererName, ordererAddr, ordererProp);
    }


    /**
     * @return org.hyperledger.fabric.sdk.Channel
     * @Description 初始化已存在的channel
     * @Param [client, channelName]
     **/
    private Channel initializeChannel(HFClient client, String channelName) throws InvalidArgumentException, TransactionException {
        //初始化channel需要使用到channel名称、初始化好的peer、
        // 另外如果交易需要共识，则需要初始化orderer
        Channel channel = client.newChannel(channelName);
        channel.addOrderer(initializeOrderer(client,ordererTlsPath,ordererName,ordererAddr));
        channel.addPeer(initializePeer(client,peerTlsPath,peerName,peerAddr));
        channel.initialize();
        return channel;
    }
}
