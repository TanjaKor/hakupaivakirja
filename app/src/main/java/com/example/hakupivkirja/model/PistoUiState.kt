package com.example.hakupivkirja.model

data class PistoUiState (
  val pistoIndex: Int, //piston index nro
  val currentMode: PistoMode = PistoMode.DEFAULT, // Represents the type ("Default", "Tyhja", "MM")
  val haukut: String? = "", // Use String for UI input, convert to Int for database, haukkujen maara haukkuvalla
  val avut: String? = "", // Use String for UI input, convert to Int for database, avut valitulla pistolla
  val palkka: String? = "", // Use String for UI input, convert to Int for database palkka valitulla pistolla
  val suoraPalkka: Boolean = false, // Default to false for UI
  val kiintoRulla: Boolean? = false, // Default to false for UI
  val irtorullanSijainti: String? = "",
  val isClosed: Boolean? = false, // Default to false for UI, onko piilo suljettu vai avoin
  // Add any other UI-specific state here, like validation errors, loading indicators, etc.
  val haukutError: String? = null, //virheiden tarkasteluun
  val isSaving: Boolean? = false,
  val selectedPistot: Int = 0, //kuinka monta pistoa on valittu
)

enum class PistoMode {
  DEFAULT,
  TYHJA,
  MM
}