package com.cgs.kerberos.builder;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.kerberos.handle.BaseTgtProcessor;
import com.cgs.kerberos.handle.DatabaseProcessor;
import com.cgs.kerberos.server.TGTHandler;
import com.cgs.kerberos.util.Serializer;

public class BaseTGTHandlerBuilder implements TGTHandlerBuilder {
	private static Logger logger = LoggerFactory.getLogger(BaseTGTHandlerBuilder.class);

	private DatabaseProcessorBuilder databaseProcessorBuilder;
	private SerializerBuilder serializerBuilder;

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public void setDatabaseProcessorBuilder(DatabaseProcessorBuilder databaseProcessorBuilder) {
		this.databaseProcessorBuilder = databaseProcessorBuilder;
	}

	public void setSerializerBuilder(SerializerBuilder serializerBuilder) {
		this.serializerBuilder = serializerBuilder;
	}

	public BaseTGTHandlerBuilder() {
		databaseProcessorBuilder = new FileDatabaseProcessorBuilder();
		serializerBuilder = new KryoSerializerBuilder();
	}

	public TGTHandler getTGTHandler(Socket socket) {
		TGTHandler instance = new TGTHandler(socket);
		Serializer s = serializerBuilder.getSerializer();
		DatabaseProcessor d = databaseProcessorBuilder.getDatabaseProcessor();
		instance.setSerializer(s);

		BaseTgtProcessor tgtProcessor = new BaseTgtProcessor();
		tgtProcessor.setDbp(d);
		tgtProcessor.setSerializer(s);
		tgtProcessor.setName(name);

		instance.setTgtProcessor(tgtProcessor);

		return instance;
	}

}
