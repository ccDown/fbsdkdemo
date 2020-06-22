package com.bsn.fbsdkdemo.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author kuan
 * Created on 2020/5/20.
 * @description
 */
public interface FabricService {

    /**
     * @Description 创建通道
     * @Param [channelName, txByte]
     * @return java.lang.String
     **/
    String createChannel(String channelName, String tx64String) throws IOException, InvalidArgumentException, TransactionException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException;

    /**
     * @Description 加入通道
     * @Param [channelByte, peer, peerOptions]
     * @return boolean
     **/
    boolean joinChannel(String channel64String,  String peerName) throws ProposalException, TransactionException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, IOException;

    /**
     * @Description 安装链码
     * @Param [channelName, chainCodeName, chainCodeVersion, chainCodePath, projectName, language]
     * @return java.util.Collection<org.hyperledger.fabric.sdk.ProposalResponse>
     **/
    Collection<ProposalResponse> installChainCode( String chainCodeName, String chainCodeVersion, String chainCodePath, String projectName, TransactionRequest.Type language) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException;

    /**
     * @Description 实例化链码
     * @Param [channelName, chainCodeName, chainCodeVersion, language, args]
     * @return java.util.Collection<org.hyperledger.fabric.sdk.ProposalResponse>
     **/
    Collection<ProposalResponse> instantiantChainCode( String channelName, String chainCodeName,String chainCodePath, String chainCodeVersion, TransactionRequest.Type language,ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException;

    /**
     * @Description 获取当前区块高度
     * @Param [channelName]
     * @return java.lang.Long
     **/
    Long getHeight(String channelName) throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException, ProposalException;

    /**
     * @Description 查询该节点已经安装的链码
     * @Param []
     * @return java.util.ArrayList<java.lang.String>
     **/
    ArrayList<String> queryInstalledChaincodes() throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, ProposalException;

    /**
     * @Description 查询该通道已实例化的链码
     * @Param [channelName]
     * @return java.util.ArrayList<java.lang.String>
     **/
    ArrayList<String> queryInstantiatedChaincodes(String channelName) throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, ProposalException, TransactionException;

    /**
     * @Description 查询链码
     * @Param [channelName, chaincodeName, func, args]
     * @return java.util.Collection<org.hyperledger.fabric.sdk.ProposalResponse>
     **/
    Collection<ProposalResponse> query(String channelName, String chaincodeName, String func, ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException;

    /**
     * @Description 调用链码
     * @Param [channelName, chaincodeName, func, args]
     * @return java.util.Collection<org.hyperledger.fabric.sdk.ProposalResponse>
     **/
    Collection<ProposalResponse> invoke(String channelName, String chaincodeName, String func, ArrayList<String> args) throws InvalidArgumentException, ProposalException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException;
}
