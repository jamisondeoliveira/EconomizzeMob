
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface ListarMovimentacoesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      EconomizzeConnector,
      ListarMovimentacoesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movimentacoes: List<MovimentacoesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class MovimentacoesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val descricao: String,
  
    val valor: Double,
  
    val data: String,
  
    val ehSaida: Boolean,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListarMovimentacoes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListarMovimentacoesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListarMovimentacoesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListarMovimentacoesQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    ListarMovimentacoesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListarMovimentacoesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListarMovimentacoesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

