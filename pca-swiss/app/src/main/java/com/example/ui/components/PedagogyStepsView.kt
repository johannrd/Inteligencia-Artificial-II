package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PcaResult
import com.example.model.SWISS_VARIABLES
import java.util.Locale

data class PcaStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

val PCA_STEPS = listOf(
    PcaStep(1, "¿Por qué PCA?", "El reto de las 6 dimensiones", Icons.Default.Psychology),
    PcaStep(2, "Estandarización", "Eliminar el sesgo de escala", Icons.Default.LinearScale),
    PcaStep(3, "Matriz de Correlación", "Capturar redundancias", Icons.Default.GridOn),
    PcaStep(4, "Autovalores y Vectores", "Rotación de ejes máximos", Icons.Default.Transform),
    PcaStep(5, "Interpretación en Swiss", "¿Qué significan PC1 y PC2?", Icons.Default.Visibility),
    PcaStep(6, "Reducción y Biplot", "De 6D al plano 2D", Icons.Default.Calculate)
)

@Composable
fun PedagogyStepsView(
    pcaResult: PcaResult,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("pedagogy_steps_screen")
    ) {
        // Pestañas de pasos con scroll horizontal
        ScrollableTabRow(
            selectedTabIndex = currentStep,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            PCA_STEPS.forEachIndexed { index, step ->
                Tab(
                    selected = currentStep == index,
                    onClick = { currentStep = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        if (currentStep == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentStep == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = step.title,
                                fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            }
        }

        // Contenido del paso actual
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "StepContentAnimation"
        ) { stepIndex ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    val step = PCA_STEPS[stepIndex]
                    StepHeader(step = step)
                }

                when (stepIndex) {
                    0 -> item { Step1WhyPca() }
                    1 -> item { Step2Standardization(pcaResult) }
                    2 -> item { Step3CorrelationMatrix(pcaResult) }
                    3 -> item { Step4EigenDecomposition(pcaResult) }
                    4 -> item { Step5SwissInterpretation(pcaResult) }
                    5 -> item { Step6ProjectionAndBiplot(pcaResult) }
                }

                item {
                    // Controles de navegación de pasos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (currentStep > 0) currentStep-- },
                            enabled = currentStep > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Anterior")
                        }

                        Button(
                            onClick = { if (currentStep < PCA_STEPS.size - 1) currentStep++ },
                            enabled = currentStep < PCA_STEPS.size - 1
                        ) {
                            Text("Siguiente Paso")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepHeader(step: PcaStep) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Paso ${step.stepNumber}: ${step.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun Step1WhyPca() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "¿Qué es PCA y qué problema resuelve?",
            content = "El Análisis de Componentes Principales (PCA) es una técnica matemática de aprendizaje no supervisado concebida por Karl Pearson (1901) y desarrollada por Harold Hotelling (1933).\n\nSu objetivo es transformar un conjunto de variables originales posiblemente correlacionadas en un nuevo conjunto de variables no correlacionadas (ortogonales) llamadas Componentes Principales (PC)."
        )

        PedagogyBox(
            title = "El Dilema del Dataset Swiss (1888)",
            content = "Tenemos 47 provincias suizas y 6 variables socioeconómicas. Visualizar un espacio de 6 dimensiones es biológicamente imposible para el ser humano.\n\nAdemás, muchas de estas variables miden aspectos solapados (por ejemplo, en regiones agrícolas la educación suele ser más baja). PCA encuentra las 2 o 3 'supervariables' maestras que resumen más del 73% de toda esa realidad sin sesgo subjetivo."
        )

        FormulaCard(
            title = "Objetivo Matemático Central",
            formula = "Maximizar Var(PC₁)  sujeto a  ||v₁|| = 1\nVar(PC₂) máxima  sujeto a  PC₂ ⊥ PC₁",
            explanation = "PC1 busca la línea recta que atraviesa la nube de datos en la dirección de mayor estiramiento o dispersión. PC2 busca la segunda mayor dirección, formando un ángulo recto exacto (ortogonal) de 90° con PC1."
        )
    }
}

@Composable
fun Step2Standardization(pcaResult: PcaResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "¿Por qué es crucial estandarizar en Swiss?",
            content = "Imagina que calculas distancias sin normalizar: la variable 'Catholic' varía entre 2.1% y 100% (desviación de 41.7), mientras que 'Infant.Mortality' varía entre 10.8 y 26.6 (desviación de 2.91).\n\nSi no estandarizamos, la variable 'Catholic' dominaría artificialmente el 90% del análisis simplemente porque su escala numérica es mayor, ¡no porque sea más importante estadísticamente!"
        )

        FormulaCard(
            title = "Fórmula del Z-Score (Estandarización)",
            formula = "Z_ij = (X_ij - μ_j) / σ_j",
            explanation = "Donde μ_j es la media de la variable j y σ_j es su desviación estándar muestral. Tras esto, todas las 6 variables tienen media = 0 y varianza = 1."
        )

        Text(
            text = "Medias y Desviaciones en el Dataset Swiss:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Tabla de medias y desviaciones
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Variable", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("Media (μ)", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("Desv (σ)", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                SWISS_VARIABLES.forEachIndexed { idx, v ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(v.nameEs, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                        Text(
                            String.format(Locale.US, "%.2f", pcaResult.means[idx]),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            String.format(Locale.US, "%.2f", pcaResult.stdDevs[idx]),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step3CorrelationMatrix(pcaResult: PcaResult) {
    val r = pcaResult.correlationMatrix

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "La Matriz de Correlación R (6x6)",
            content = "Como todas las variables están estandarizadas, la matriz de covarianza de Z es exactamente igual a la Matriz de Correlación de Pearson R = (1 / (n - 1)) * Zᵀ * Z.\n\nEsta matriz contiene toda la información de asociaciones lineales entre las 6 variables."
        )

        // Resaltar correlaciones clave
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Fuerte positiva (+0.70)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Educación y Examen Militar avanzaban de la mano en los cantones suizos.", fontSize = 10.sp)
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Fuerte negativa (-0.69)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary)
                    Text("A mayor dedicación Agraria, menor calificación en Examen Militar (-0.69) y Educación (-0.64).", fontSize = 10.sp)
                }
            }
        }

        Text(
            text = "Matriz R calculada directamente en Swiss:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Matriz visual simplificada con scroll horizontal
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Column {
                    // Cabecera columnas
                    Row {
                        Text("", modifier = Modifier.width(90.dp))
                        SWISS_VARIABLES.forEach { v ->
                            Text(
                                text = v.id.take(4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.width(48.dp)
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    SWISS_VARIABLES.forEachIndexed { i, vi ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vi.nameEs.take(10),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(90.dp)
                            )
                            SWISS_VARIABLES.indices.forEach { j ->
                                val valR = r[i][j]
                                val color = when {
                                    i == j -> Color.DarkGray
                                    valR > 0.4 -> Color(0xFF0F766E)
                                    valR < -0.4 -> Color(0xFFB91C1C)
                                    else -> Color.Gray
                                }
                                Text(
                                    text = String.format(Locale.US, "%+.2f", valR),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = color,
                                    fontWeight = if (i == j || valR > 0.4 || valR < -0.4) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.width(48.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step4EigenDecomposition(pcaResult: PcaResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "Descomposición Espectral: R · v = λ · v",
            content = "El teorema espectral garantiza que toda matriz simétrica y semidefinida positiva (como nuestra matriz de correlación R) se descompone en p autovalores reales (λ) y autovectores ortonormales (v).\n\n- Los Autovectores (v_k) indican la DIRECCIÓN exacta de los nuevos ejes en el hiperespacio.\n- Los Autovalores (λ_k) indican la CANTIDAD DE VARIANZA absorbida por ese eje.\n- La suma total de autovalores es exactamente igual al número de variables: Σ λ_k = 6.0."
        )

        Text(
            text = "Los 6 Componentes Resultantes de Swiss:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Componente", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                    Text("Autovalor (λ)", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("Varianza %", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("Acumulado %", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                pcaResult.eigenvalues.indices.forEach { idx ->
                    val isKaiser = pcaResult.eigenvalues[idx] >= 1.0
                    val isElbow = (idx == 1)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "PC${idx + 1}",
                                fontWeight = if (isElbow) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isElbow) Color(0xFFEA580C) else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                            if (isElbow) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("📍Codo", fontSize = 9.sp, color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            String.format(Locale.US, "%.4f", pcaResult.eigenvalues[idx]),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isKaiser) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            String.format(Locale.US, "%.2f%%", pcaResult.varianceExplainedPct[idx]),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            String.format(Locale.US, "%.1f%%", pcaResult.cumulativeVariancePct[idx]),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (idx == 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step5SwissInterpretation(pcaResult: PcaResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "¿Qué significan los Componentes en la Suiza de 1888?",
            content = "Un componente principal no es solo un número abstracto: al examinar sus Cargas Factoriales (loadings), descubrimos los fenómenos socioeconómicos subyacentes que gobernaban la Suiza del siglo XIX."
        )

        // Interpretación de PC1
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PC1 (53.3% de la varianza): Modernización vs Tradición Rural",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Cargas Negativas: Examen Militar (-0.51), Educación (-0.45)\n• Cargas Positivas: Fertilidad (+0.46), Agricultura (+0.42), Catolicismo (+0.35)",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "👉 Significado: Separa los cantones urbanos, educados y militarmente sobresalientes (como Ginebra, Lausana y Neuchâtel en el extremo izquierdo negativo) de los cantones predominantemente rurales y tradicionales con alta fertilidad (como Sierre, Glâne y Hérens en el extremo positivo).",
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // Interpretación de PC2
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PC2 (19.8% de la varianza): Mortalidad Infantil y Sanidad",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Carga Dominante Negativa: Mortalidad Infantil (-0.81), Fertilidad (-0.32)\n• Carga Positiva: Agricultura (+0.41)",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "👉 Significado: Eje sanitario. Distingue provincias con severa mortalidad infantil (valores muy negativos en PC2 como Porrentruy y Courtelary) de aquellas zonas montañosas agrícolas con tasas de mortalidad infantil significativamente más bajas (como Conthey y Hérens).",
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun Step6ProjectionAndBiplot(pcaResult: PcaResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PedagogyBox(
            title = "Proyección y Reducción: De 6 Dimensiones a un Plano 2D",
            content = "Cada provincia se proyecta multiplicando su fila estandarizada Z_i por los vectores propios v₁ y v₂:\nScore_PC1 = Σ Z_ij · v_j1\nScore_PC2 = Σ Z_ij · v_j2\n\nEl resultado es el 'Biplot', donde vemos simultáneamente la posición geográfica/socioeconómica de las 47 provincias y las flechas de las 6 variables originales."
        )

        FormulaCard(
            title = "Ecuación de Proyección en Matriz",
            formula = "Scores (47 x 2) = Z (47 x 6) · V_sub (6 x 2)",
            explanation = "¡Redujimos el tamaño de la matriz de datos en un 66.7%, conservando el 73.13% de toda la variabilidad original!"
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Ve a la pestaña 'Biplot 2D' para interactuar directamente con la proyección de cada provincia y observar los vectores de cada variable.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PedagogyBox(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun FormulaCard(title: String, formula: String, explanation: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formula,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
