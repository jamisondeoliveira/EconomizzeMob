
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



public interface ExcluirMovimentacaoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      EconomizzeConnector,
      ExcluirMovimentacaoMutation.Data,
      ExcluirMovimentacaoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movimentacao_delete: MovimentacaoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ExcluirMovimentacao"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ExcluirMovimentacaoMutation.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ExcluirMovimentacaoMutation.Data,
    ExcluirMovimentacaoMutation.Variables
  > =
  ref(
    
      ExcluirMovimentacaoMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun ExcluirMovimentacaoMutation.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ExcluirMovimentacaoMutation.Data,
    ExcluirMovimentacaoMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


