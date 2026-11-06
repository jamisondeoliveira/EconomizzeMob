
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.example.economizzemob.dataconnect

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface EconomizzeConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<EconomizzeConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val excluirMovimentacao: ExcluirMovimentacaoMutation
  
    public val listarMovimentacoes: ListarMovimentacoesQuery
  
    public val salvarMovimentacao: SalvarMovimentacaoMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "economizze",
      location = "southamerica-east1",
      serviceId = "economizze-dd05d-service",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):EconomizzeConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        EconomizzeConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, EconomizzeConnectorImpl>()

    
  }
}

public val EconomizzeConnector.Companion.instance:EconomizzeConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config
  ))

public fun EconomizzeConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):EconomizzeConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun EconomizzeConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):EconomizzeConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class EconomizzeConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : EconomizzeConnector {
  
    override val excluirMovimentacao by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ExcluirMovimentacaoMutationImpl(this)
    }
  
    override val listarMovimentacoes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListarMovimentacoesQueryImpl(this)
    }
  
    override val salvarMovimentacao by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SalvarMovimentacaoMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<EconomizzeConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<EconomizzeConnector, *, *>> =
    listOf(
      excluirMovimentacao,
        salvarMovimentacao,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<EconomizzeConnector, *, *>> =
    listOf(
      listarMovimentacoes,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    EconomizzeConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is EconomizzeConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "EconomizzeConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "EconomizzeConnectorImpl(dataConnect=$dataConnect)"
}



private open class EconomizzeConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: EconomizzeConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<EconomizzeConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: EconomizzeConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    EconomizzeConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    EconomizzeConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    EconomizzeConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is EconomizzeConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "EconomizzeConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "EconomizzeConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class EconomizzeConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: EconomizzeConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<EconomizzeConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: EconomizzeConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    EconomizzeConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    EconomizzeConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    EconomizzeConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is EconomizzeConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "EconomizzeConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "EconomizzeConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class ExcluirMovimentacaoMutationImpl(
  connector: EconomizzeConnector
):
  ExcluirMovimentacaoMutation,
  EconomizzeConnectorGeneratedMutationImpl<
      ExcluirMovimentacaoMutation.Data,
      ExcluirMovimentacaoMutation.Variables
  >(
    connector,
    ExcluirMovimentacaoMutation.Companion.operationName,
    ExcluirMovimentacaoMutation.Companion.dataDeserializer,
    ExcluirMovimentacaoMutation.Companion.variablesSerializer,
  )


private class ListarMovimentacoesQueryImpl(
  connector: EconomizzeConnector
):
  ListarMovimentacoesQuery,
  EconomizzeConnectorGeneratedQueryImpl<
      ListarMovimentacoesQuery.Data,
      Unit
  >(
    connector,
    ListarMovimentacoesQuery.Companion.operationName,
    ListarMovimentacoesQuery.Companion.dataDeserializer,
    ListarMovimentacoesQuery.Companion.variablesSerializer,
  )


private class SalvarMovimentacaoMutationImpl(
  connector: EconomizzeConnector
):
  SalvarMovimentacaoMutation,
  EconomizzeConnectorGeneratedMutationImpl<
      SalvarMovimentacaoMutation.Data,
      SalvarMovimentacaoMutation.Variables
  >(
    connector,
    SalvarMovimentacaoMutation.Companion.operationName,
    SalvarMovimentacaoMutation.Companion.dataDeserializer,
    SalvarMovimentacaoMutation.Companion.variablesSerializer,
  )


