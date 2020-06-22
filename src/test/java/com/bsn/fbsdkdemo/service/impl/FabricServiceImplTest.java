package com.bsn.fbsdkdemo.service.impl;

import com.bsn.fbsdkdemo.service.FabricService;
import com.bsn.fbsdkdemo.utils.FileUtils;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kuan
 * Created on 2020/5/22.
 * @description
 */
@Slf4j
@SpringBootTest
class FabricServiceImplTest {

    @Autowired
    private FabricService fabricService;

    String txFilePath = "D:\\JavaProject\\fbsdkdemo\\src\\main\\resources\\channelTxs\\";
    String channelStringPath = "D:\\JavaProject\\fbsdkdemo\\src\\main\\resources\\channelString\\";

    @Test
    void createChannel() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, InvalidArgumentException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
//        String channelName = "channel202005231219";
        String channelName = "channel202005231257";
//        String channelName = "channel202005231256";
        String tx64String = Base64.getEncoder().encodeToString(FileUtils.readFile(txFilePath+ channelName + ".tx"));
        String channel64String = fabricService.createChannel(channelName, tx64String);
        FileUtils.writeFile(channel64String, channelStringPath + channelName + ".txt", false);
        log.info("createChannel channel64String=【{}】", channel64String);
    }

    @Test
    void joinChannel() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, ProposalException, InvalidArgumentException, IOException, CryptoException, ClassNotFoundException, TransactionException {
        String channelName = "channel202005231219";
//        String channelName = "channel202005231256";
        String channel64String = new String(FileUtils.readFile(channelStringPath + channelName + ".txt"));
        String peerName = "peer2.orgbnode.bsnbase.com";
        boolean result = fabricService.joinChannel(channel64String, peerName);
        log.info("joinChannel result=【{}】", result);
    }

    @Test
    void installChainCode() throws IllegalAccessException, InstantiationException, ProposalException, NoSuchMethodException, InvalidArgumentException, InvocationTargetException, CryptoException, ClassNotFoundException, TransactionException {
        String channelName= "channel202005231219";
        String chainCodeName = "bsnBaseCC";
        String chainCodeVersion = "1.0.0";
        String chainCodePath = "D:\\JavaProject\\fbsdkdemo\\src\\test\\resources\\go";
        String projectName = "bsnBaseCC";
        TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;

        Collection<ProposalResponse> proposals = fabricService.installChainCode(chainCodeName,chainCodeVersion,chainCodePath,projectName,language);
        for (ProposalResponse response : proposals) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful transaction proposal response Txid: {} from peer {}}", response.getTransactionID(), response.getPeer().getName());
            } else {
                log.info("Faild tracsaction proposal response");
            }
        }

    }

    @Test
    void instantiantChainCode() throws IllegalAccessException, InstantiationException, ProposalException, NoSuchMethodException, InvalidArgumentException, InvocationTargetException, CryptoException, ClassNotFoundException, TransactionException {
        String channelName= "channel202005231219";
        String chainCodeName = "bsnBaseCC";
        String chainCodePath = "bsnBaseCC";
        String chainCodeVersion = "1.0.0";
        TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;
        ArrayList<String> args = new ArrayList<>();

        Collection<ProposalResponse> proposals = fabricService.instantiantChainCode(channelName,chainCodeName,chainCodePath,chainCodeVersion,language,args);
        for (ProposalResponse response : proposals) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful transaction proposal response Txid: {} from peer {}},payload: {}", response.getTransactionID(), response.getPeer().getName(),response.getChaincodeActionResponsePayload());
            } else {
                log.info("Faild tracsaction proposal response, reason: {}",response.getMessage());
            }
        }
    }

    @Test
    void getHeight() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, InvalidArgumentException, CryptoException, ClassNotFoundException, TransactionException, ProposalException {
        String channelName = "channel202005231219";
        Long height = fabricService.getHeight(channelName);
        log.info("getHeight result= 【{}】",height);

    }

    @Test
    void queryInstalledChaincodes() throws NoSuchMethodException, InvalidArgumentException, InstantiationException, ClassNotFoundException, ProposalException, IllegalAccessException, InvocationTargetException, CryptoException {
        ArrayList<String> arrayList = fabricService.queryInstalledChaincodes();
        arrayList.forEach(log::info);

    }

    @Test
    void queryInstantiatedChaincodes() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, InvalidArgumentException, CryptoException, ClassNotFoundException, TransactionException, ProposalException {
        String channelName = "channel202005231219";
        ArrayList<String> arrayList = fabricService.queryInstantiatedChaincodes(channelName);
        arrayList.forEach(log::info);
    }

    @Test
    void query() throws IllegalAccessException, InstantiationException, ProposalException, NoSuchMethodException, InvalidArgumentException, InvocationTargetException, CryptoException, ClassNotFoundException, TransactionException {
        String channelName = "channel202005231219";
        String chainCodeName = "bsnBaseCC";
        String func = "get";
        ArrayList<String> params = new ArrayList<>();
        params.add("Say2");


        Collection<ProposalResponse> proposals = fabricService.query(channelName,chainCodeName,func,params);
        for (ProposalResponse response : proposals) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful transaction proposal response Txid: {} from peer {}},payload: {}", response.getTransactionID(), response.getPeer().getName(),new java.lang.String(response.getChaincodeActionResponsePayload()));
            } else {
                log.info("Faild tracsaction proposal response, reason: {}",response.getMessage());
            }
        }
    }

    @Test
    void invoke() throws IllegalAccessException, InstantiationException, ProposalException, NoSuchMethodException, InvalidArgumentException, InvocationTargetException, CryptoException, ClassNotFoundException, TransactionException {
        String channelName = "channel202005231219";
        String chainCodeName = "bsnBaseCC";
        String func = "set";
        ArrayList<String> params = new ArrayList<>();
        params.add("{\"baseKey\":\"Say2\",\"baseValue\":\"Hey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautifulHey girl,you'r beautiful宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽" +
                "宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽宽\"}");

        Collection<ProposalResponse> proposals = fabricService.invoke(channelName,chainCodeName,func,params);
        for (ProposalResponse response : proposals) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful transaction proposal response Txid: {} from peer {}},payload: {}", response.getTransactionID(), response.getPeer().getName(),new java.lang.String(response.getChaincodeActionResponsePayload()));
            } else {
                log.info("Faild tracsaction proposal response, reason: {}",response.getMessage());
            }
        }
    }
}