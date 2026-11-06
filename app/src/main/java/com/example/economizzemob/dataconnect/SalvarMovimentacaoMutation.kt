
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



public interface SalvarMovimentacaoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      EconomizzeConnector,
      SalvarMovimentacaoMutation.Data,
      SalvarMovimentacaoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val descricao: String,
  
    val valor: Double,
  
    val data: String,
  
    val ehSaida: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movimentacao_upsert: MovimentacaoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SalvarMovimentacao"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SalvarMovimentacaoMutation.ref(
  
    id: java.util.UUID,descricao: String,valor: Double,data: String,ehSaida: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    SalvarMovimentacaoMutation.Data,
    SalvarMovimentacaoMutation.Variables
  > =
  ref(
    
      SalvarMovimentacaoMutation.Variables(
        id=id,descricao=descricao,valor=valor,data=data,ehSaida=ehSaida,
  
      )
    
  )

public suspend fun SalvarMovimentacaoMutation.execute(

  
    
      id: java.util.UUID,descricao: String,valor: Double,data: String,ehSaida: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    SalvarMovimentacaoMutation.Data,
    SalvarMovimentacaoMutation.Variables
  > =
  ref(
    
      id=id,descricao=descricao,valor=valor,data=data,ehSaida=ehSaida,
  
    
  ).execute()


