package th.co.cjexpress.store.sage.presentation.ui.screen.requestDiscountRtc

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc.RequestDiscountItemsActivity
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonDateCounter
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonNumberCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemDetailViewModel
import timber.log.Timber
import java.util.Date

@Composable
fun RequestDiscountItemDetailScreen(viewModel: RequestDiscountItemDetailViewModel = hiltViewModel()) {
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(state.errorData)
        }
        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is RequestDiscountItemDetailViewModel.UIState.ValidateBarcodeBdState -> {
                    viewModel.resetEditingProductLD()
                    context
                        .startActivity(Intent(context, RequestDiscountItemsActivity::class.java))
                        .also {
                            (context as ComponentActivity).finish()
                        }
                }
            }
        }

        else -> {}
    }

    RequestDiscountItemDetailScreenContainer()
}

@Composable
private fun RequestDiscountItemDetailScreenContainer() {
    Scaffold(
        topBar = {
            RequestDiscountItemDetailScreen_TopAppBarContainer()
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            RequestDiscountItemDetailScreen_ProductDetailsContainer(
                modifier =
                    Modifier
                        .weight(1f),
            )

            RequestDiscountItemDetailScreen_BottomButtonContainer()
        }
    }
}

@Composable
private fun RequestDiscountItemDetailScreen_TopAppBarContainer(viewModel: RequestDiscountItemDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current

    TachyonTopAppBar(
        titleText =
            if (viewModel.getBdDetailLD()?.documentNumber?.isNotEmpty() == true) {
                stringResource(R.string.text_edit_discount_doc)
            } else {
                stringResource(R.string.text_create_discount_doc)
            },
        onNavigationIconClick = {
            viewModel.logEventOnBackPressed()
            context.startActivity(Intent(context, RequestDiscountItemsActivity::class.java)).also {
                viewModel.resetEditingProductLD()
                (context as ComponentActivity).finish()
            }
        },
    )
}

@Composable
private fun RequestDiscountItemDetailScreen_ProductDetailsContainer(
    modifier: Modifier,
    viewModel: RequestDiscountItemDetailViewModel = hiltViewModel(),
) {
    val editingProduct = viewModel.getEditingProductLD()
    val isNewItem = viewModel.getIsNewItemLD()
    val inputExpiryDateState by viewModel.inputExpiryDateState.collectAsState()
    val actualQtyState by viewModel.inputActualQtyState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        TachyonText(
            text = stringResource(R.string.text_product_info),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .padding(TachyonDictionary.space.padding._md),
        ) {
            TachyonText(
                text = editingProduct?.barcode.orEmpty().ifEmpty { "N/A" },
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                text = editingProduct?.productName.orEmpty().ifEmpty { "N/A" },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
        InputExpireContainer(
            expiryDate = inputExpiryDateState ?: Date(),
            enable = isNewItem,
            onDateSelected = {
                viewModel.setInputExpiryDateState(it)
            },
        )
        ActualQtyCounterContainer(
            modifier = Modifier,
            actualQty = actualQtyState,
            onActualQtyChange = {
                viewModel.setInputActualQtyState(it)
            },
        )
    }
}

@Composable
private fun InputExpireContainer(
    expiryDate: Date,
    enable: Boolean,
    onDateSelected: (Date) -> Unit,
) {
    Timber.tag("STATE").i("InputExpireContainer expiryDate = $expiryDate")

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TachyonDictionary.surface.static._default2)
                .padding(TachyonDictionary.space.padding._md),
    ) {
        TachyonText(
            text = stringResource(R.string.text_select_expiry_date),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
        TachyonDateCounter(
            modifier =
                Modifier
                    .fillMaxWidth(),
            defaultDate = expiryDate,
            enabled = enable,
            minDate = Date(),
            onValueChange = {
                Timber.tag("STATE").d("DateCounter on ValueChange = $it")
                onDateSelected(it)
            },
        )
    }
}

@Composable
private fun ActualQtyCounterContainer(
    modifier: Modifier,
    actualQty: Int?,
    onActualQtyChange: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .padding(TachyonDictionary.space.padding._md),
        ) {
            TachyonText(
                text = stringResource(R.string.text_input_quantity),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
            TachyonNumberCounter(
                modifier = Modifier.fillMaxWidth(),
                initialNumber = actualQty ?: 0,
                maxNumber = 999,
                onIncrement = {
                    Timber.tag("STATE").d("TachyonNumberCounter on ValueChange = $it")
                    onActualQtyChange(it)
                },
                onDecrement = {
                    Timber.tag("STATE").d("TachyonNumberCounter on ValueChange = $it")
                    onActualQtyChange(it)
                },
            )
        }
    }
}

@Composable
private fun RequestDiscountItemDetailScreen_BottomButtonContainer(viewModel: RequestDiscountItemDetailViewModel = hiltViewModel()) {
    val isNewItem = viewModel.getIsNewItemLD()
    val editingProduct = viewModel.getEditingProductLD()
    val inputExpiryDate = viewModel.inputExpiryDateState.collectAsState()
    val inputRequestQty = viewModel.inputActualQtyState.collectAsState()
    val discountItemIsAvailable =
        editingProduct?.let { it.barcode.isNotEmpty() && it.productName.isNotEmpty() } == true

    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonSolidButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            buttonText = stringResource(R.string.text_ok),
            style = TachyonSolidButtonStyle.GENERAL,
            enabled = discountItemIsAvailable,
            onClick = {
                val liveData = viewModel.getBdDetailLD()
                liveData?.discountType?.name?.let {
                    editingProduct?.barcode?.let { barcode ->
                        viewModel.validateBarcode(
                            isNewItem = isNewItem,
                            discountType = it,
                            barcode = barcode,
                            expiryDate = inputExpiryDate.value ?: Date(),
                            requestQty = inputRequestQty.value ?: 0,
                        )
                    }
                }
            },
        )
    }
}
