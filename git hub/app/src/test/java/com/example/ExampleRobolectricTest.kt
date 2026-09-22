package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PcaEngine
import com.example.model.SWISS_DATASET
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PCA Swiss", appName)
  }

  @Test
  fun `verify swiss dataset and pca calculations`() {
    val result = PcaEngine.computePca(SWISS_DATASET)

    // 47 provinces and 6 variables
    assertEquals(47, result.projectedProvinces.size)
    assertEquals(6, result.eigenvalues.size)

    // Sum of eigenvalues equals number of standardized variables (6.0)
    val sumEigen = result.eigenvalues.sum()
    assertEquals(6.0, sumEigen, 0.05)

    // Decreasing order
    for (i in 0 until 5) {
      assertTrue(result.eigenvalues[i] >= result.eigenvalues[i + 1])
    }

    // Kaiser count = 2 (PC1 and PC2 with eigenvalue >= 1.0)
    assertEquals(2, result.kaiserCount)

    // PC1 variance explained > 50%
    assertTrue(result.varianceExplainedPct[0] > 50.0)

    // Cumulative variance PC1 + PC2 > 70%
    assertTrue(result.cumulativeVariancePct[1] > 70.0)
  }
}

