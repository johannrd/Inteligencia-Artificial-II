package com.example.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa una provincia suiza francófona en el censo histórico de 1888.
 * Dataset clásico 'swiss' utilizado internacionalmente en estadística y econometría.
 */
data class SwissProvince(
    val name: String,
    val fertility: Double,       // Medida estandarizada de fecundidad común (Ig)
    val agriculture: Double,     // % de hombres dedicados a la agricultura
    val examination: Double,     // % de reclutas con máxima calificación en examen militar
    val education: Double,       // % de educación más allá de la primaria para reclutas
    val catholic: Double,        // % de población católica (vs protestante)
    val infantMortality: Double  // % de nacidos vivos que fallecen antes de cumplir 1 año
) {
    fun toArray(): DoubleArray = doubleArrayOf(
        fertility,
        agriculture,
        examination,
        education,
        catholic,
        infantMortality
    )
}

data class VariableMeta(
    val id: String,
    val nameEs: String,
    val unit: String,
    val description: String,
    val historicalContext: String
)

val SWISS_VARIABLES = listOf(
    VariableMeta(
        id = "Fertility",
        nameEs = "Fertilidad",
        unit = "Índice Ig",
        description = "Medida estandarizada de fecundidad marital de Coale.",
        historicalContext = "En 1888, regiones rurales tradicionales tenían tasas mucho más elevadas."
    ),
    VariableMeta(
        id = "Agriculture",
        nameEs = "Agricultura",
        unit = "% de hombres",
        description = "% de hombres económicamente activos en el sector agrícola.",
        historicalContext = "Refleja economías tradicionales vs. industrializadas (relojería/textil)."
    ),
    VariableMeta(
        id = "Examination",
        nameEs = "Examen Militar",
        unit = "% alta nota",
        description = "% de reclutas militares que obtuvieron la calificación 'draftee' más alta.",
        historicalContext = "Prueba rigurosa suiza de aptitud física, matemática y cívica a los 20 años."
    ),
    VariableMeta(
        id = "Education",
        nameEs = "Educación",
        unit = "% secundaria+",
        description = "% de reclutas con escolaridad más allá de la educación primaria obligatoria.",
        historicalContext = "Indicador de modernización institucional y acceso a escuelas secundarias."
    ),
    VariableMeta(
        id = "Catholic",
        nameEs = "Población Católica",
        unit = "% católica",
        description = "% de población católica romana (en contraposición a protestante/reformada).",
        historicalContext = "Suiza presentaba marcadas divisiones culturales y religiosas cantonales."
    ),
    VariableMeta(
        id = "InfantMortality",
        nameEs = "Mortalidad Infantil",
        unit = "% < 1 año",
        description = "% de recién nacidos vivos que fallecían antes de cumplir el primer año.",
        historicalContext = "Indicador directo de higiene sanitaria, atención médica y nutrición."
    )
)

/**
 * Las 47 provincias históricas de Suiza de habla francesa (1888).
 */
val SWISS_DATASET: List<SwissProvince> = listOf(
    SwissProvince("Courtelary", 80.2, 17.0, 15.0, 12.0, 9.96, 22.2),
    SwissProvince("Delemont", 83.1, 45.1, 6.0, 9.0, 84.84, 22.2),
    SwissProvince("Franches-Mnt", 92.5, 39.7, 5.0, 5.0, 93.40, 20.2),
    SwissProvince("Moutier", 85.8, 36.5, 12.0, 7.0, 33.77, 20.3),
    SwissProvince("Neuveville", 76.9, 43.5, 17.0, 15.0, 5.16, 20.6),
    SwissProvince("Porrentruy", 76.1, 35.3, 9.0, 7.0, 90.57, 26.6),
    SwissProvince("Broye", 83.8, 70.2, 16.0, 7.0, 92.85, 23.6),
    SwissProvince("Glane", 92.4, 67.8, 14.0, 8.0, 97.16, 24.9),
    SwissProvince("Gruyere", 82.4, 53.3, 12.0, 7.0, 97.67, 21.0),
    SwissProvince("Sarine", 82.9, 45.2, 16.0, 13.0, 91.38, 24.4),
    SwissProvince("Veveyse", 87.1, 64.5, 14.0, 6.0, 98.61, 24.5),
    SwissProvince("Aigle", 64.1, 62.0, 21.0, 12.0, 8.52, 16.5),
    SwissProvince("Aubonne", 66.9, 67.5, 14.0, 7.0, 2.27, 19.1),
    SwissProvince("Avenches", 68.9, 60.7, 19.0, 12.0, 4.43, 22.7),
    SwissProvince("Cossonay", 61.7, 69.3, 22.0, 5.0, 2.82, 18.7),
    SwissProvince("Echallens", 68.3, 72.6, 18.0, 2.0, 24.20, 21.2),
    SwissProvince("Grandson", 71.7, 34.0, 17.0, 8.0, 3.30, 20.0),
    SwissProvince("Lausanne", 55.7, 19.4, 26.0, 28.0, 12.11, 20.2),
    SwissProvince("La Vallee", 54.3, 15.2, 31.0, 20.0, 2.15, 10.8),
    SwissProvince("Lavaux", 65.1, 73.0, 19.0, 9.0, 2.84, 20.0),
    SwissProvince("Morges", 65.5, 59.8, 22.0, 10.0, 5.23, 18.0),
    SwissProvince("Moudon", 65.0, 55.1, 14.0, 3.0, 4.52, 22.4),
    SwissProvince("Nyone", 56.6, 50.9, 22.0, 12.0, 15.14, 16.7),
    SwissProvince("Orbe", 57.4, 54.1, 20.0, 6.0, 4.20, 15.3),
    SwissProvince("Oron", 72.5, 71.2, 12.0, 1.0, 2.40, 21.0),
    SwissProvince("Payerne", 74.2, 58.1, 14.0, 8.0, 5.23, 23.8),
    SwissProvince("Paysd'enhaut", 72.0, 63.5, 6.0, 3.0, 2.56, 18.0),
    SwissProvince("Rolle", 60.5, 60.8, 16.0, 10.0, 7.72, 16.3),
    SwissProvince("Vevey", 58.3, 26.8, 25.0, 19.0, 18.46, 20.9),
    SwissProvince("Yverdon", 65.4, 49.5, 15.0, 8.0, 6.10, 22.5),
    SwissProvince("Conthey", 75.5, 85.9, 3.0, 2.0, 99.71, 15.1),
    SwissProvince("Entremont", 69.3, 84.9, 7.0, 6.0, 99.68, 19.8),
    SwissProvince("Herens", 77.3, 89.7, 5.0, 2.0, 100.0, 18.3),
    SwissProvince("Martigwy", 70.5, 78.2, 12.0, 6.0, 98.96, 19.4),
    SwissProvince("Monthey", 79.4, 64.9, 7.0, 3.0, 98.22, 20.2),
    SwissProvince("St Maurice", 65.0, 75.9, 9.0, 9.0, 99.06, 17.8),
    SwissProvince("Sierre", 92.2, 84.6, 3.0, 3.0, 99.46, 16.3),
    SwissProvince("Sion", 79.3, 63.1, 13.0, 13.0, 96.83, 18.1),
    SwissProvince("Boudry", 70.4, 38.4, 26.0, 12.0, 5.62, 20.3),
    SwissProvince("La Chauxdfnd", 65.7, 7.7, 29.0, 11.0, 13.79, 20.5),
    SwissProvince("Le Locle", 72.7, 16.7, 22.0, 13.0, 11.22, 18.9),
    SwissProvince("Neuchatel", 64.4, 17.6, 35.0, 32.0, 16.92, 23.0),
    SwissProvince("Val de Ruz", 77.6, 37.6, 15.0, 7.0, 4.97, 20.0),
    SwissProvince("ValdeTravers", 67.6, 18.7, 25.0, 7.0, 8.65, 19.5),
    SwissProvince("V. De Geneve", 35.0, 1.2, 37.0, 53.0, 42.34, 18.0),
    SwissProvince("Rive Droite", 44.7, 46.6, 16.0, 29.0, 50.43, 18.2),
    SwissProvince("Rive Gauche", 42.8, 27.7, 22.0, 29.0, 58.33, 19.3)
)

data class ProjectedProvince(
    val province: SwissProvince,
    val scores: DoubleArray // PC1, PC2, PC3, PC4, PC5, PC6
)

data class VariableLoading(
    val variable: VariableMeta,
    val loadings: DoubleArray // carga en cada PC
)

data class PcaResult(
    val means: DoubleArray,
    val stdDevs: DoubleArray,
    val standardizedData: Array<DoubleArray>,
    val correlationMatrix: Array<DoubleArray>,
    val eigenvalues: DoubleArray,
    val varianceExplainedPct: DoubleArray,
    val cumulativeVariancePct: DoubleArray,
    val eigenvectors: Array<DoubleArray>, // [variableIdx][pcIdx]
    val variableLoadings: List<VariableLoading>,
    val projectedProvinces: List<ProjectedProvince>,
    val elbowIndex: Int,       // Índice del codo (0-based, ej. 1 para PC2)
    val kaiserCount: Int       // Cuántos componentes tienen eigenvalue >= 1.0
)

/**
 * Motor de cálculo de Análisis de Componentes Principales (PCA).
 * Implementa de forma transparente cada paso algebraico:
 * 1. Estandarización (Z = (X - μ) / σ)
 * 2. Matriz de Correlación de Pearson R (6x6)
 * 3. Descomposición espectral mediante rotaciones de Jacobi
 * 4. Ordenamiento decreciente de componentes principales
 * 5. Proyección de observaciones (Scores = Z * V)
 * 6. Detección algorítmica del Codo (Scree Plot Elbow) y Criterio de Kaiser
 */
object PcaEngine {

    fun computePca(dataset: List<SwissProvince> = SWISS_DATASET): PcaResult {
        val n = dataset.size
        val p = 6
        val rawMatrix = dataset.map { it.toArray() }

        // 1. Medias
        val means = DoubleArray(p)
        for (j in 0 until p) {
            var sum = 0.0
            for (i in 0 until n) {
                sum += rawMatrix[i][j]
            }
            means[j] = sum / n
        }

        // 2. Desviaciones estándar muestrales (ddof = 1)
        val stdDevs = DoubleArray(p)
        for (j in 0 until p) {
            var sumSq = 0.0
            for (i in 0 until n) {
                val diff = rawMatrix[i][j] - means[j]
                sumSq += diff * diff
            }
            stdDevs[j] = sqrt(sumSq / (n - 1))
        }

        // 3. Matriz Estandarizada Z (n x p)
        val zMatrix = Array(n) { i ->
            DoubleArray(p) { j ->
                (rawMatrix[i][j] - means[j]) / stdDevs[j]
            }
        }

        // 4. Matriz de Correlación R = (1 / (n - 1)) * Z^T * Z
        val correlationMatrix = Array(p) { j ->
            DoubleArray(p) { k ->
                if (j == k) 1.0 else {
                    var sumProd = 0.0
                    for (i in 0 until n) {
                        sumProd += zMatrix[i][j] * zMatrix[i][k]
                    }
                    sumProd / (n - 1)
                }
            }
        }

        // 5. Autovalores y Autovectores mediante el algoritmo de Jacobi
        val (rawEigvals, rawEigvecs) = jacobiEigenDecomposition(correlationMatrix, p)

        // 6. Ordenar componentes de mayor a menor autovalor
        val order = (0 until p).sortedByDescending { rawEigvals[it] }
        val sortedEigvals = DoubleArray(p) { i -> rawEigvals[order[i]] }
        val sortedEigvecs = Array(p) { row ->
            DoubleArray(p) { col ->
                rawEigvecs[row][order[col]]
            }
        }

        // 7. Varianza explicada y acumulada
        val sumEigvals = sortedEigvals.sum()
        val varianceExplainedPct = DoubleArray(p) { i -> (sortedEigvals[i] / sumEigvals) * 100.0 }
        val cumulativeVariancePct = DoubleArray(p)
        var acc = 0.0
        for (i in 0 until p) {
            acc += varianceExplainedPct[i]
            cumulativeVariancePct[i] = acc
        }

        // 8. Cargas factoriales (Loadings)
        val loadingsList = (0 until p).map { varIdx ->
            VariableLoading(
                variable = SWISS_VARIABLES[varIdx],
                loadings = DoubleArray(p) { pcIdx -> sortedEigvecs[varIdx][pcIdx] }
            )
        }

        // 9. Proyección de cada provincia (Scores = Z * V)
        val projected = dataset.mapIndexed { i, province ->
            val scores = DoubleArray(p) { pcIdx ->
                var score = 0.0
                for (varIdx in 0 until p) {
                    score += zMatrix[i][varIdx] * sortedEigvecs[varIdx][pcIdx]
                }
                score
            }
            ProjectedProvince(province = province, scores = scores)
        }

        // 10. Criterio de Kaiser: autovalores >= 1.0
        val kaiserCount = sortedEigvals.count { it >= 1.0 }

        // 11. Detección del Codo (Elbow): máxima diferencia de pendientes (segunda derivada discreta)
        var elbowIndex = 1 // Por defecto PC2 (índice 1)
        var maxCurvature = -1.0
        for (k in 1 until p - 1) {
            val slopeBefore = sortedEigvals[k - 1] - sortedEigvals[k]
            val slopeAfter = sortedEigvals[k] - sortedEigvals[k + 1]
            val curvature = slopeBefore - slopeAfter
            if (curvature > maxCurvature) {
                maxCurvature = curvature
                elbowIndex = k
            }
        }

        return PcaResult(
            means = means,
            stdDevs = stdDevs,
            standardizedData = zMatrix,
            correlationMatrix = correlationMatrix,
            eigenvalues = sortedEigvals,
            varianceExplainedPct = varianceExplainedPct,
            cumulativeVariancePct = cumulativeVariancePct,
            eigenvectors = sortedEigvecs,
            variableLoadings = loadingsList,
            projectedProvinces = projected,
            elbowIndex = elbowIndex,
            kaiserCount = kaiserCount
        )
    }

    /**
     * Algoritmo clásico y exacto de rotaciones de Jacobi para matrices simétricas reales.
     * Garantiza convergencia cuadrática hacia una matriz diagonal de autovalores.
     */
    private fun jacobiEigenDecomposition(
        matrix: Array<DoubleArray>,
        n: Int,
        maxIterations: Int = 100
    ): Pair<DoubleArray, Array<DoubleArray>> {
        val a = Array(n) { i -> matrix[i].clone() }
        val v = Array(n) { i -> DoubleArray(n) { j -> if (i == j) 1.0 else 0.0 } }

        for (iter in 0 until maxIterations) {
            var maxOffDiag = 0.0
            var p = 0
            var q = 1
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    val absVal = abs(a[i][j])
                    if (absVal > maxOffDiag) {
                        maxOffDiag = absVal
                        p = i
                        q = j
                    }
                }
            }

            if (maxOffDiag < 1e-12) break

            val app = a[p][p]
            val aqq = a[q][q]
            val apq = a[p][q]
            val theta = 0.5 * atan2(2.0 * apq, aqq - app)
            val c = cos(theta)
            val s = sin(theta)

            for (k in 0 until n) {
                if (k != p && k != q) {
                    val akp = a[k][p]
                    val akq = a[k][q]
                    a[k][p] = c * akp - s * akq
                    a[p][k] = a[k][p]
                    a[k][q] = s * akp + c * akq
                    a[q][k] = a[k][q]
                }
            }
            a[p][p] = c * c * app - 2.0 * s * c * apq + s * s * aqq
            a[q][q] = s * s * app + 2.0 * s * c * apq + c * c * aqq
            a[p][q] = 0.0
            a[q][p] = 0.0

            for (k in 0 until n) {
                val vkp = v[k][p]
                val vkq = v[k][q]
                v[k][p] = c * vkp - s * vkq
                v[k][q] = s * vkp + c * vkq
            }
        }

        val eigenvalues = DoubleArray(n) { a[it][it] }
        return Pair(eigenvalues, v)
    }
}
