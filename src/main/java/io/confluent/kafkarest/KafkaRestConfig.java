/**
 * Copyright 2015 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.confluent.kafkarest;

import java.util.Map;
import java.util.Properties;

import io.confluent.common.config.ConfigDef;
import io.confluent.common.config.ConfigDef.Importance;
import io.confluent.common.config.ConfigDef.Type;
import io.confluent.rest.RestConfig;
import io.confluent.rest.RestConfigException;
import org.apache.kafka.common.protocol.SecurityProtocol;

import static io.confluent.common.config.ConfigDef.Range.atLeast;

/**
 * Settings for the REST proxy server.
 */
public class KafkaRestConfig extends RestConfig {

  public static final String ID_CONFIG = "id";
  private static final String
      ID_CONFIG_DOC =
      "Unique ID for this REST server instance. This is used in generating unique IDs for consumers that do "
      + "not specify their ID. The ID is empty by default, which makes a single server setup easier to "
      + "get up and running, but is not safe for multi-server deployments where automatic consumer IDs "
      + "are used.";
  public static final String ID_DEFAULT = "";

  public static final String HOST_NAME_CONFIG = "host.name";
  private static final String HOST_NAME_DOC =
      "The host name used to generate absolute URLs in responses. If empty, the default canonical"
      + " hostname is used";
  public static final String HOST_NAME_DEFAULT = "";

  public static final String ZOOKEEPER_CONNECT_CONFIG = "zookeeper.connect";
  private static final String
      ZOOKEEPER_CONNECT_DOC =
      "Specifies the ZooKeeper connection string in the form "
      + "hostname:port where host and port are the host and port of a ZooKeeper server. To allow connecting "
      + "through other ZooKeeper nodes when that ZooKeeper machine is down you can also specify multiple hosts "
      + "in the form hostname1:port1,hostname2:port2,hostname3:port3.\n"
      + "\n"
      + "The server may also have a ZooKeeper chroot path as part of it's ZooKeeper connection string which puts "
      + "its data under some path in the global ZooKeeper namespace. If so the consumer should use the same "
      + "chroot path in its connection string. For example to give a chroot path of /chroot/path you would give "
      + "the connection string as hostname1:port1,hostname2:port2,hostname3:port3/chroot/path.";
  public static final String ZOOKEEPER_CONNECT_DEFAULT = "localhost:2181";

  public static final String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
  private static final String
      BOOTSTRAP_SERVERS_DOC =
      "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. "
      + "The client will make use of all servers irrespective of which servers are specified here for "
      + "bootstrapping—this list only impacts the initial hosts used to discover the full set of servers. "
      + "This list should be in the form host1:port1,host2:port2,.... Since these servers are just used for the "
      + "initial connection to discover the full cluster membership (which may change dynamically), "
      + "this list need not contain the full set of servers (you may want more than one, though, "
      + "in case a server is down).";

  public static final String BOOTSTRAP_SERVERS_DEFAULT = "PLAINTEXT://localhost:9092";
  public static final String KAFKACLIENT_CONNECTION_URL_DEFAULT = "localhost:2181";
  public static final String SCHEMA_REGISTRY_URL_CONFIG = "schema.registry.url";
  private static final String SCHEMA_REGISTRY_URL_DOC =
      "The base URL for the schema registry that should be used by the Avro serializer.";
  private static final String SCHEMA_REGISTRY_URL_DEFAULT = "http://localhost:8081";

  public static final String PRODUCER_THREADS_CONFIG = "producer.threads";
  private static final String
      PRODUCER_THREADS_DOC =
      "Number of threads to run produce requests on.";
  public static final String PRODUCER_THREADS_DEFAULT = "5";

  public static final String CONSUMER_ITERATOR_TIMEOUT_MS_CONFIG = "consumer.iterator.timeout.ms";
  private static final String
      CONSUMER_ITERATOR_TIMEOUT_MS_DOC =
      "Timeout for blocking consumer iterator operations. "
      + "This should be set to a small enough value that it is possible to effectively peek() on the iterator.";
  public static final String CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT = "1";

  public static final String CONSUMER_ITERATOR_BACKOFF_MS_CONFIG = "consumer.iterator.backoff.ms";
  private static final String
      CONSUMER_ITERATOR_BACKOFF_MS_DOC =
      "Amount of time to backoff when an iterator runs "
      + "out of data. If a consumer has a dedicated worker thread, this is effectively the maximum error for the "
      + "entire request timeout. It should be small enough to closely target the timeout, but large enough to "
      + "avoid busy waiting.";
  public static final String CONSUMER_ITERATOR_BACKOFF_MS_DEFAULT = "50";

  public static final String CONSUMER_REQUEST_TIMEOUT_MS_CONFIG = "consumer.request.timeout.ms";
  private static final String
      CONSUMER_REQUEST_TIMEOUT_MS_DOC =
      "The maximum total time to wait for messages for a "
      + "request if the maximum number of messages has not yet been reached.";
  public static final String CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT = "1000";

  public static final String CONSUMER_REQUEST_MAX_BYTES_CONFIG = "consumer.request.max.bytes";
  private static final String
      CONSUMER_REQUEST_MAX_BYTES_DOC =
      "Maximum number of bytes in unencoded message keys and values returned by a single "
      + "request. This can be used by administrators to limit the memory used by a single "
      + "consumer and to control the memory usage required to decode responses on clients that "
      + "cannot perform a streaming decode. Note that the actual payload will be larger due to "
      + "overhead from base64 encoding the response data and from JSON encoding the entire "
      + "response.";
  public static final long CONSUMER_REQUEST_MAX_BYTES_DEFAULT = 64 * 1024 * 1024;

  public static final String CONSUMER_THREADS_CONFIG = "consumer.threads";
  private static final String
      CONSUMER_THREADS_DOC =
      "Number of threads to run consumer requests on.";
  public static final String CONSUMER_THREADS_DEFAULT = "1";

  public static final String CONSUMER_INSTANCE_TIMEOUT_MS_CONFIG = "consumer.instance.timeout.ms";
  private static final String
      CONSUMER_INSTANCE_TIMEOUT_MS_DOC =
      "Amount of idle time before a consumer instance "
      + "is automatically destroyed.";
  public static final String CONSUMER_INSTANCE_TIMEOUT_MS_DEFAULT = "300000";

  public static final String SIMPLE_CONSUMER_MAX_POOL_SIZE_CONFIG = "simpleconsumer.pool.size.max";
  private static final String
      SIMPLE_CONSUMER_MAX_POOL_SIZE_DOC =
      "Maximum number of SimpleConsumers that can be instantiated per broker."
      + " If 0, then the pool size is not limited.";
  public static final String SIMPLE_CONSUMER_MAX_POOL_SIZE_DEFAULT = "25";

  public static final String SIMPLE_CONSUMER_POOL_TIMEOUT_MS_CONFIG = "simpleconsumer.pool.timeout.ms";
  private static final String
      SIMPLE_CONSUMER_POOL_TIMEOUT_MS_DOC =
      "Amount of time to wait for an available SimpleConsumer from the pool before failing."
          + " Use 0 for no timeout";
  public static final String SIMPLE_CONSUMER_POOL_TIMEOUT_MS_DEFAULT = "1000";

  // TODO: change this to "http://0.0.0.0:8082" when PORT_CONFIG is deleted.
  private static final String KAFKAREST_LISTENERS_DEFAULT = "";
  private static final int KAFKAREST_PORT_DEFAULT = 8082;

  private static final String METRICS_JMX_PREFIX_DEFAULT_OVERRIDE = "kafka.rest";

  public static final String KAFKACLIENT_CONNECTION_URL_CONFIG = "client.connection.url";
  public static final String KAFKACLIENT_BOOTSTRAP_SERVERS_CONFIG = "client.bootstrap.servers";
  /**
   * <code>client.zk.session.timeout.ms</code>
   */
  public static final String KAFKACLIENT_ZK_SESSION_TIMEOUT_MS_CONFIG
      = "client.zk.session.timeout.ms";
  public static final String KAFKACLIENT_TIMEOUT_CONFIG = "client.timeout.ms";
  /**
   * <code>client.init.timeout.ms</code>
   */
  public static final String KAFKACLIENT_INIT_TIMEOUT_CONFIG = "client.init.timeout.ms";

  public static final String ZOOKEEPER_SET_ACL_CONFIG = "zookeeper.set.acl";
  public static final String KAFKACLIENT_SECURITY_PROTOCOL_CONFIG =
      "client.security.protocol";
  public static final String KAFKACLIENT_SSL_TRUSTSTORE_LOCATION_CONFIG =
      "client.ssl.truststore.location";
  public static final String KAFKACLIENT_SSL_TRUSTSTORE_PASSWORD_CONFIG =
      "client.ssl.truststore.password";
  public static final String KAFKACLIENT_SSL_KEYSTORE_LOCATION_CONFIG =
      "client.ssl.keystore.location";
  public static final String KAFKACLIENT_SSL_TRUSTSTORE_TYPE_CONFIG =
          "client.ssl.truststore.type";
  public static final String KAFKACLIENT_SSL_TRUSTMANAGER_ALGORITHM_CONFIG =
          "client.ssl.trustmanager.algorithm";
  public static final String KAFKACLIENT_SSL_KEYSTORE_PASSWORD_CONFIG =
      "client.ssl.keystore.password";
  public static final String KAFKACLIENT_SSL_KEYSTORE_TYPE_CONFIG =
          "client.ssl.keystore.type";
  public static final String KAFKACLIENT_SSL_KEYMANAGER_ALGORITHM_CONFIG =
          "client.ssl.keymanager.algorithm";
  public static final String KAFKACLIENT_SSL_KEY_PASSWORD_CONFIG =
      "client.ssl.key.password";
  public static final String KAFKACLIENT_SSL_ENABLED_PROTOCOLS_CONFIG =
      "client.ssl.enabled.protocols";
  public static final String KAFKACLIENT_SSL_PROTOCOL_CONFIG =
      "client.ssl.protocol";
  public static final String KAFKACLIENT_SSL_PROVIDER_CONFIG =
      "client.ssl.provider";
  public static final String KAFKACLIENT_SSL_CIPHER_SUITES_CONFIG =
      "client.ssl.cipher.suites";
  public static final String KAFKACLIENT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG =
      "client.ssl.endpoint.identification.algorithm";
  public static final String KAFKACLIENT_SASL_KERBEROS_SERVICE_NAME_CONFIG =
      "client.sasl.kerberos.service.name";
  public static final String KAFKACLIENT_SASL_MECHANISM_CONFIG =
      "client.sasl.mechanism";
  public static final String KAFKACLIENT_SASL_KERBEROS_KINIT_CMD_CONFIG =
      "client.sasl.kerberos.kinit.cmd";
  public static final String KAFKACLIENT_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN_CONFIG =
      "client.sasl.kerberos.min.time.before.relogin";
  public static final String KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_JITTER_CONFIG =
      "client.sasl.kerberos.ticket.renew.jitter";
  public static final String KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR_CONFIG =
      "client.sasl.kerberos.ticket.renew.window.factor";

  protected static final String KAFKACLIENT_CONNECTION_URL_DOC =
      "Zookeeper url for the Kafka cluster";
  protected static final String KAFKACLIENT_BOOTSTRAP_SERVERS_DOC =
      "A list of Kafka brokers to connect to. For example, `PLAINTEXT://hostname:9092,SSL://hostname2:9092`\n"
      + "\n"
      + "If this configuration is not specified, the Schema Registry's internal Kafka clients will get their Kafka bootstrap server list\n"
      + "from ZooKeeper (configured with `client.connection.url`). Note that if `client.bootstrap.servers` is configured,\n"
      + "`client.connection.url` still needs to be configured, too.\n"
      + "\n"
      + "This configuration is particularly important when Kafka security is enabled, because Kafka may expose multiple endpoints that\n"
      + "all will be stored in ZooKeeper, but Kafka REST  may need to be configured with just one of those endpoints.";
  protected static final String KAFKACLIENT_ZK_SESSION_TIMEOUT_MS_DOC =
      "Zookeeper session timeout";
  protected static final String KAFKACLIENT_INIT_TIMEOUT_DOC =
      "The timeout for initialization of the Kafka store, including creation of the Kafka topic "
      + "that stores schema data.";
  protected static final String KAFKACLIENT_TIMEOUT_DOC =
      "The timeout for an operation on the Kafka store";
  protected static final String ZOOKEEPER_SET_ACL_DOC =
      "Whether or not to set an ACL in ZooKeeper when znodes are created and ZooKeeper SASL authentication is "
      + "configured. IMPORTANT: if set to `true`, the SASL principal must be the same as the Kafka brokers.";
  protected static final String KAFKACLIENT_SECURITY_PROTOCOL_DOC =
      "The security protocol to use when connecting with Kafka, the underlying persistent storage. "
      + "Values can be `PLAINTEXT`, `SSL`, `SASL_PLAINTEXT`, or `SASL_SSL`.";
  protected static final String KAFKACLIENT_SSL_TRUSTSTORE_LOCATION_DOC =
      "The location of the SSL trust store file.";
  protected static final String KAFKACLIENT_SSL_TRUSTSTORE_PASSWORD_DOC =
      "The password to access the trust store.";
  protected static final String KAFAKSTORE_SSL_TRUSTSTORE_TYPE_DOC =
      "The file format of the trust store.";
  protected static final String KAFKACLIENT_SSL_TRUSTMANAGER_ALGORITHM_DOC =
      "The algorithm used by the trust manager factory for SSL connections.";
  protected static final String KAFKACLIENT_SSL_KEYSTORE_LOCATION_DOC =
      "The location of the SSL keystore file.";
  protected static final String KAFKACLIENT_SSL_KEYSTORE_PASSWORD_DOC =
      "The password to access the keystore.";
  protected static final String KAFAKSTORE_SSL_KEYSTORE_TYPE_DOC =
      "The file format of the keystore.";
  protected static final String KAFKACLIENT_SSL_KEYMANAGER_ALGORITHM_DOC =
      "The algorithm used by key manager factory for SSL connections.";
  protected static final String KAFKACLIENT_SSL_KEY_PASSWORD_DOC =
      "The password of the key contained in the keystore.";
  protected static final String KAFAKSTORE_SSL_ENABLED_PROTOCOLS_DOC =
      "Protocols enabled for SSL connections.";
  protected static final String KAFAKSTORE_SSL_PROTOCOL_DOC =
      "The SSL protocol used.";
  protected static final String KAFAKSTORE_SSL_PROVIDER_DOC =
      "The name of the security provider used for SSL.";
  protected static final String KAFKACLIENT_SSL_CIPHER_SUITES_DOC =
      "A list of cipher suites used for SSL.";
  protected static final String KAFKACLIENT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC =
      "The endpoint identification algorithm to validate the server hostname using the server certificate.";
  public static final String KAFKACLIENT_SASL_KERBEROS_SERVICE_NAME_DOC =
      "The Kerberos principal name that the Kafka client runs as. This can be defined either in the JAAS "
      + "config file or here.";
  public static final String KAFKACLIENT_SASL_MECHANISM_DOC =
      "The SASL mechanism used for Kafka connections. GSSAPI is the default.";
  public static final String KAFKACLIENT_SASL_KERBEROS_KINIT_CMD_DOC =
      "The Kerberos kinit command path.";
  public static final String KAFKACLIENT_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN_DOC =
      "The login time between refresh attempts.";
  public static final String KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_JITTER_DOC =
      "The percentage of random jitter added to the renewal time.";
  public static final String KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR_DOC =
      "Login thread will sleep until the specified window factor of time from last refresh to ticket's expiry has "
      + "been reached, at which time it will try to renew the ticket.";
  private static final boolean ZOOKEEPER_SET_ACL_DEFAULT = false;
  private static final ConfigDef config;

  static {
    config = baseConfigDef()
        .defineOverride(PORT_CONFIG, ConfigDef.Type.INT, KAFKAREST_PORT_DEFAULT,
                        ConfigDef.Importance.LOW, PORT_CONFIG_DOC)
        .defineOverride(LISTENERS_CONFIG, ConfigDef.Type.LIST, KAFKAREST_LISTENERS_DEFAULT,
                        ConfigDef.Importance.HIGH, LISTENERS_DOC)
        .defineOverride(RESPONSE_MEDIATYPE_PREFERRED_CONFIG, Type.LIST,
                        Versions.PREFERRED_RESPONSE_TYPES, Importance.LOW,
                        RESPONSE_MEDIATYPE_PREFERRED_CONFIG_DOC)
        .defineOverride(RESPONSE_MEDIATYPE_DEFAULT_CONFIG, Type.STRING,
                        Versions.KAFKA_MOST_SPECIFIC_DEFAULT, Importance.LOW,
                        RESPONSE_MEDIATYPE_DEFAULT_CONFIG_DOC)
        .defineOverride(METRICS_JMX_PREFIX_CONFIG, Type.STRING,
                        METRICS_JMX_PREFIX_DEFAULT_OVERRIDE, Importance.LOW, METRICS_JMX_PREFIX_DOC)
        .define(ID_CONFIG, Type.STRING, ID_DEFAULT, Importance.HIGH, ID_CONFIG_DOC)
        .define(HOST_NAME_CONFIG, Type.STRING, HOST_NAME_DEFAULT, Importance.MEDIUM, HOST_NAME_DOC)
        .define(ZOOKEEPER_CONNECT_CONFIG, Type.STRING, ZOOKEEPER_CONNECT_DEFAULT,
                Importance.HIGH, ZOOKEEPER_CONNECT_DOC)
        .define(BOOTSTRAP_SERVERS_CONFIG, Type.STRING, BOOTSTRAP_SERVERS_DEFAULT,
                Importance.HIGH, BOOTSTRAP_SERVERS_DOC)
        .define(SCHEMA_REGISTRY_URL_CONFIG, Type.STRING, SCHEMA_REGISTRY_URL_DEFAULT,
                Importance.HIGH, SCHEMA_REGISTRY_URL_DOC)
        .define(PRODUCER_THREADS_CONFIG, Type.INT, PRODUCER_THREADS_DEFAULT,
                Importance.LOW, PRODUCER_THREADS_DOC)
        .define(CONSUMER_ITERATOR_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT,
                Importance.LOW, CONSUMER_ITERATOR_TIMEOUT_MS_DOC)
        .define(CONSUMER_ITERATOR_BACKOFF_MS_CONFIG, Type.INT, CONSUMER_ITERATOR_BACKOFF_MS_DEFAULT,
                Importance.LOW, CONSUMER_ITERATOR_BACKOFF_MS_DOC)
        .define(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT,
                Importance.MEDIUM, CONSUMER_REQUEST_TIMEOUT_MS_DOC)
        .define(CONSUMER_REQUEST_MAX_BYTES_CONFIG, Type.LONG,
                CONSUMER_REQUEST_MAX_BYTES_DEFAULT,
                Importance.MEDIUM, CONSUMER_REQUEST_MAX_BYTES_DOC)
        .define(CONSUMER_THREADS_CONFIG, Type.INT, CONSUMER_THREADS_DEFAULT,
                Importance.MEDIUM, CONSUMER_THREADS_DOC)
        .define(CONSUMER_INSTANCE_TIMEOUT_MS_CONFIG, Type.INT, CONSUMER_INSTANCE_TIMEOUT_MS_DEFAULT,
                Importance.LOW, CONSUMER_INSTANCE_TIMEOUT_MS_DOC)
        .define(SIMPLE_CONSUMER_MAX_POOL_SIZE_CONFIG, Type.INT, SIMPLE_CONSUMER_MAX_POOL_SIZE_DEFAULT,
                Importance.MEDIUM, SIMPLE_CONSUMER_MAX_POOL_SIZE_DOC)
        .define(SIMPLE_CONSUMER_POOL_TIMEOUT_MS_CONFIG, Type.INT, SIMPLE_CONSUMER_POOL_TIMEOUT_MS_DEFAULT,
                Importance.LOW, SIMPLE_CONSUMER_POOL_TIMEOUT_MS_DOC)
        .define(KAFKACLIENT_CONNECTION_URL_CONFIG, ConfigDef.Type.STRING, KAFKACLIENT_CONNECTION_URL_DEFAULT,
		ConfigDef.Importance.HIGH, KAFKACLIENT_CONNECTION_URL_DOC)
        .define(KAFKACLIENT_BOOTSTRAP_SERVERS_CONFIG, ConfigDef.Type.LIST, "", ConfigDef.Importance.MEDIUM,
                KAFKACLIENT_CONNECTION_URL_DOC)
        .define(KAFKACLIENT_ZK_SESSION_TIMEOUT_MS_CONFIG, ConfigDef.Type.INT, 30000, atLeast(0),
                ConfigDef.Importance.LOW, KAFKACLIENT_ZK_SESSION_TIMEOUT_MS_DOC)
        .define(KAFKACLIENT_INIT_TIMEOUT_CONFIG, ConfigDef.Type.INT, 60000, atLeast(0),
                ConfigDef.Importance.MEDIUM, KAFKACLIENT_INIT_TIMEOUT_DOC)
        .define(KAFKACLIENT_TIMEOUT_CONFIG, ConfigDef.Type.INT, 500, atLeast(0),
                ConfigDef.Importance.MEDIUM, KAFKACLIENT_TIMEOUT_DOC)
        .define(KAFKACLIENT_SECURITY_PROTOCOL_CONFIG, ConfigDef.Type.STRING,
                SecurityProtocol.PLAINTEXT.toString(), ConfigDef.Importance.MEDIUM,
                KAFKACLIENT_SECURITY_PROTOCOL_DOC)
        .define(KAFKACLIENT_SSL_TRUSTSTORE_LOCATION_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.HIGH,
                KAFKACLIENT_SSL_TRUSTSTORE_LOCATION_DOC)
        .define(KAFKACLIENT_SSL_TRUSTSTORE_PASSWORD_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.HIGH,
                KAFKACLIENT_SSL_TRUSTSTORE_PASSWORD_DOC)
        .define(KAFKACLIENT_SSL_TRUSTSTORE_TYPE_CONFIG, ConfigDef.Type.STRING,
                "JKS", ConfigDef.Importance.MEDIUM,
                KAFAKSTORE_SSL_TRUSTSTORE_TYPE_DOC)
        .define(KAFKACLIENT_SSL_TRUSTMANAGER_ALGORITHM_CONFIG, ConfigDef.Type.STRING,
                "PKIX", ConfigDef.Importance.LOW,
                KAFKACLIENT_SSL_TRUSTMANAGER_ALGORITHM_DOC)
        .define(KAFKACLIENT_SSL_KEYSTORE_LOCATION_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.HIGH,
                KAFKACLIENT_SSL_KEYSTORE_LOCATION_DOC)
        .define(KAFKACLIENT_SSL_KEYSTORE_PASSWORD_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.HIGH,
                KAFKACLIENT_SSL_KEYSTORE_PASSWORD_DOC)
        .define(KAFKACLIENT_SSL_KEYSTORE_TYPE_CONFIG, ConfigDef.Type.STRING,
                "JKS", ConfigDef.Importance.MEDIUM,
                KAFAKSTORE_SSL_KEYSTORE_TYPE_DOC)
        .define(KAFKACLIENT_SSL_KEYMANAGER_ALGORITHM_CONFIG, ConfigDef.Type.STRING,
                "SunX509", ConfigDef.Importance.LOW,
                KAFKACLIENT_SSL_KEYMANAGER_ALGORITHM_DOC)
        .define(KAFKACLIENT_SSL_KEY_PASSWORD_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.HIGH,
                KAFKACLIENT_SSL_KEY_PASSWORD_DOC)
        .define(KAFKACLIENT_SSL_ENABLED_PROTOCOLS_CONFIG, ConfigDef.Type.STRING,
                "TLSv1.2,TLSv1.1,TLSv1", ConfigDef.Importance.MEDIUM,
                KAFAKSTORE_SSL_ENABLED_PROTOCOLS_DOC)
        .define(KAFKACLIENT_SSL_PROTOCOL_CONFIG, ConfigDef.Type.STRING,
                "TLS", ConfigDef.Importance.MEDIUM,
                KAFAKSTORE_SSL_PROTOCOL_DOC)
        .define(KAFKACLIENT_SSL_PROVIDER_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.MEDIUM,
                KAFAKSTORE_SSL_PROVIDER_DOC)
        .define(KAFKACLIENT_SSL_CIPHER_SUITES_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.LOW,
                KAFKACLIENT_SSL_CIPHER_SUITES_DOC)
        .define(KAFKACLIENT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.LOW,
                KAFKACLIENT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC)
        .define(KAFKACLIENT_SASL_KERBEROS_SERVICE_NAME_CONFIG, ConfigDef.Type.STRING,
                "", ConfigDef.Importance.MEDIUM,
                KAFKACLIENT_SASL_KERBEROS_SERVICE_NAME_DOC)
        .define(KAFKACLIENT_SASL_MECHANISM_CONFIG, ConfigDef.Type.STRING,
                "GSSAPI", ConfigDef.Importance.MEDIUM,
                KAFKACLIENT_SASL_MECHANISM_DOC)
        .define(KAFKACLIENT_SASL_KERBEROS_KINIT_CMD_CONFIG, ConfigDef.Type.STRING,
                "/usr/bin/kinit", ConfigDef.Importance.LOW,
                KAFKACLIENT_SASL_KERBEROS_KINIT_CMD_DOC)
        .define(KAFKACLIENT_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN_CONFIG, ConfigDef.Type.LONG,
                60000, ConfigDef.Importance.LOW,
                KAFKACLIENT_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN_DOC)
        .define(KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_JITTER_CONFIG, ConfigDef.Type.DOUBLE,
                0.05, ConfigDef.Importance.LOW,
                KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_JITTER_DOC)
        .define(KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR_CONFIG, ConfigDef.Type.DOUBLE,
                0.8, ConfigDef.Importance.LOW,
                KAFKACLIENT_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR_DOC);

  }

  private Time time;
  private Properties originalProperties;

  public KafkaRestConfig() throws RestConfigException {
    this(new Properties());
  }

  public KafkaRestConfig(String propsFile) throws RestConfigException {
    this(getPropsFromFile(propsFile));
  }

  public KafkaRestConfig(Properties props) throws RestConfigException {
    this(props, new SystemTime());
  }

  public KafkaRestConfig(Properties props, Time time) throws RestConfigException {
    super(config, props);
    this.originalProperties = props;
    this.time = time;
  }

  public Time getTime() {
    return time;
  }

  public Properties getOriginalProperties() {
    return originalProperties;
  }

  private Properties addExistingV1Properties(Properties props) {
      //copy over the properties excluding those with prefix
      //"ssl.", "sasl.", "client.", "producer.", "consumer."
      for (Map.Entry<?, ?> e : originalProperties.entrySet()) {
          String name = (String) e.getKey();
          if ( name.startsWith("ssl.") || name.startsWith("sasl.") ||
	       name.startsWith("client.") || name.startsWith("producer.") ||
	       name.startsWith("consumer.")) {
	      continue;
	  }
	  
	  props.setProperty(name, originalProperties.getProperty(name));
      }
      return props;
  }
    
  private Properties addPropertiesWithPrefix(String prefix, Properties props) {
      //copy over the properties with prefix 
      int prefixLen = prefix.length();
      for (Map.Entry<?, ?> e : originalProperties.entrySet()) {
          String name = (String) e.getKey();
          if (name.startsWith(prefix)) {
              String newName = name.substring(prefixLen);
              props.setProperty(newName, originalProperties.getProperty(name));
          }
      }
      return props;
  }

  public Properties getProducerProperties() {
      Properties producerProps = new Properties();
      //add properties for V1 version of configuration parameters for backward compability
      //since producers need to support V1 with configuration change
      addExistingV1Properties(producerProps);
      //copy over the properties with prefixes "client." and "producer."
      addPropertiesWithPrefix("client.", producerProps);
      addPropertiesWithPrefix("producer.", producerProps);

      return producerProps;
  }

  public Properties getConsumerProperties() {
      Properties consumerProps = new Properties();
      //copy cover the properties with prefixes "client." and  "consumer."
      addPropertiesWithPrefix("client.", consumerProps);
      addPropertiesWithPrefix("consumer.", consumerProps);
      return consumerProps;
  }
    
  public static void main(String[] args) {
    System.out.print(config.toRst());
  }
}
