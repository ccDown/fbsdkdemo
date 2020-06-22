package com.bsn.fbsdkdemo.controller;

import com.bsn.fbsdkdemo.service.FabricService;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author kuan
 * Created on 2020/5/20.
 * @description
 */

@RestController
@RequestMapping("fabric")
public class FabricController {

    @Autowired
    private FabricService fabricService;

    @RequestMapping("getHeight/{channelName}")
    private Long getHeight(@PathVariable("channelName")  String channelName) {
        try {
            return fabricService.getHeight(channelName);
        } catch (NoSuchMethodException | InvocationTargetException | InvalidArgumentException | InstantiationException | IllegalAccessException | CryptoException | ClassNotFoundException | TransactionException | ProposalException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @RequestMapping("queryInstalledChaincodes")
    private ArrayList<String> queryInstalledChaincodes() {
        try {
            return fabricService.queryInstalledChaincodes();
        } catch (NoSuchMethodException | InvocationTargetException | InvalidArgumentException | InstantiationException | IllegalAccessException | CryptoException | ClassNotFoundException  | ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("queryInstantiatedChaincodes/{channelName}")
    private ArrayList<String> queryInstantiatedChaincodes(@PathVariable("channelName")  String channelName) {
        try {
            return fabricService.queryInstantiatedChaincodes(channelName);
        } catch (NoSuchMethodException | InvocationTargetException | InvalidArgumentException | InstantiationException | IllegalAccessException | CryptoException | ClassNotFoundException | TransactionException | ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("query/{channelName}/{chaincodeName}/{func}/{args}")
    private Collection<ProposalResponse> query(
            @PathVariable("channelName")  String channelName,
            @PathVariable("chaincodeName")  String chaincodeName,
            @PathVariable("func")  String func,
            @PathVariable("args")  ArrayList<String> args) {
        try {
            return fabricService.query(channelName,chaincodeName,func,args);
        } catch (NoSuchMethodException | InvocationTargetException | InvalidArgumentException | InstantiationException | IllegalAccessException | CryptoException | ClassNotFoundException | TransactionException | ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("invoke/{channelName}/{chaincodeName}/{func}/{args}")
    private Collection<ProposalResponse> invoke(
            @PathVariable("channelName")  String channelName,
            @PathVariable("chaincodeName")  String chaincodeName,
            @PathVariable("func")  String func,
            @PathVariable("args")  ArrayList<String> args) {
        try {
            return fabricService.invoke(channelName,chaincodeName,func,args);
        } catch (NoSuchMethodException | InvocationTargetException | InvalidArgumentException | InstantiationException | IllegalAccessException | CryptoException | ClassNotFoundException | TransactionException | ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

}
