package th.co.cjexpress.store.sage.presentation.ui.screen.requestDiscountRtc

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc.RequestDiscountDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc.RequestDiscountItemDetailActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryFilled
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryOutlined
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDD_MM_YYYYFormat
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

@Composable
fun RequestDiscountItemsScreen(viewModel: RequestDiscountItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val editingProduct by viewModel.editingProductFlow.collectAsState(null)
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val showRemoveDialogState by viewModel.showRemoveItemDialogState.collectAsState()

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> TachyonAlertDialog(dialogData = state.errorData)
        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is RequestDiscountItemsViewModel.UIState.BdDocumentSubmitState -> {
                    TachyonAlertDialog(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.SUCCESS,
                            titleText = stringResource(id = R.string.text_save_success),
                            messageText =
                                stringResource(
                                    id = R.string.text_format_shipment_document_no,
                                    state.data.data?.documentNumber ?: "",
                                ),
                            onPositiveClick = {
                                context
                                    .startActivity(
                                        Intent(
                                            context,
                                            RequestDiscountDocsActivity::class.java,
                                        ),
                                    ).also {
                                        (context as ComponentActivity).finish()
                                    }
                            },
                        ),
                    )
                }
            }
        }

        else -> {}
    }

    showRemoveDialogState?.let {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.ERROR,
                titleText = stringResource(id = R.string.text_confirm_to_delete),
                messageText =
                    stringResource(
                        id = R.string.text_confirm_to_remove_item_by_barcode,
                        it.barcode,
                    ),
                negativeTextRes = R.string.text_no,
                onNegativeClick = {
                    viewModel.hideRemoveItemDialog()
                },
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.removeProduct(it)
                },
            ),
        )
    }

    if (editingProduct != null) {
        context.startActivity(Intent(context, RequestDiscountItemDetailActivity::class.java)).also {
            (context as ComponentActivity).finish()
        }
    }

    RequestDiscountItemsScreenContainer()
}

@Composable
fun RequestDiscountItemsScreenContainer() {
    Scaffold(
        topBar = {
            RequestDiscountItemsScreen_TopAppBarContainer()
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            RequestDiscountItemsScreen_BarcodeInputContainer()

            RequestDiscountItemsScreen_DocsDetailContainer()

            RequestDiscountItemsScreen_BarcodeListContainer(
                modifier = Modifier.weight(1f),
            )

            RequestDiscountItemsScreen_BottomButtonContainer()
        }
    }
}

@Composable
fun RequestDiscountItemsScreen_TopAppBarContainer(viewModel: RequestDiscountItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val bdDetailModel = viewModel.bdDetailModel.collectAsState()

    TachyonTopAppBar(
        titleText =
            if (bdDetailModel.value?.documentNumber?.isNotEmpty() == true) {
                stringResource(R.string.text_edit_discount_doc)
            } else {
                stringResource(R.string.text_create_discount_doc)
            },
        onNavigationIconClick = {
            viewModel.logEventOnBackPressed()
            context.startActivity(Intent(context, RequestDiscountDocsActivity::class.java)).also {
                (context as ComponentActivity).finish()
            }
        },
    )
}

@Composable
fun RequestDiscountItemsScreen_DocsDetailContainer(viewModel: RequestDiscountItemsViewModel = hiltViewModel()) {
    val bdDetailModel by viewModel.bdDetailModel.collectAsState()
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TachyonDictionary.surface.static._default2)
                .padding(TachyonDictionary.space.padding._md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonText(
                text = stringResource(R.string.text_discount_created_date),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text = bdDetailModel?.createdDate.orEmpty().ifEmpty { "N/A" },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonText(
                text = stringResource(R.string.text_discount_doc_info),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text = bdDetailModel?.documentNumber.orEmpty().ifEmpty { "N/A" },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonText(
                text =
                    if (!bdDetailModel?.discountType?.text.isNullOrEmpty()) {
                        "(${bdDetailModel?.discountType?.text})"
                    } else {
                        "N/A"
                    },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
    }
}

@Composable
fun RequestDiscountItemsScreen_BarcodeInputContainer(viewModel: RequestDiscountItemsViewModel = hiltViewModel()) {
    val softKeyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var inputBarcode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        focusRequester.requestFocus()
        softKeyboardController?.hide()
    }

    TachyonOutlinedTextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        keyboardActions =
            KeyboardActions(
                onDone =
                    safeKeyboardActionWithDebounce {
                        viewModel.validateBarcode(barcode = inputBarcode)
                    },
            ),
        value = inputBarcode,
        onValueChange = { inputBarcode = it },
        label = {
            TachyonText(
                text = stringResource(R.string.text_product_barcode),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._description,
            )
        },
        placeholder = {
            TachyonText(
                text = stringResource(R.string.text_please_fill_or_scan),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._placeholder,
            )
        },
        trailingIcon = {
            if (inputBarcode.isNotEmpty()) {
                IconButton(
                    onClick = safeClickWithDebounce { inputBarcode = "" },
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.text_clear),
                    )
                }
            }
        },
    )
}

@Composable
fun RequestDiscountItemsScreen_BarcodeListContainer(
    modifier: Modifier,
    viewModel: RequestDiscountItemsViewModel = hiltViewModel(),
) {
    val bdDetailModel by viewModel.bdDetailModel.collectAsState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        TachyonText(
            text = stringResource(R.string.text_product_list, bdDetailModel?.products?.size ?: 0),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        if (!bdDetailModel?.products.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
            ) {
                items(
                    items = bdDetailModel?.products.orEmpty(),
                    key = { product -> "${product.barcode}_${product.expiredDate!!.toStringDD_MM_YYYYFormat()}" },
                ) { product ->
                    RequestDiscountItemsScreen_BarcodeDetailsContainer(
                        productData = product,
                        onBarcodeSelected = {
                            viewModel.setEditingProductLD(product)
                        },
                        onRemoveItem = {
                            viewModel.showRemoveItemDialog(product)
                        },
                    )
                }
            }
        } else {
            TachyonEmptyLazyColumn(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.icon_empty_document),
                description = "ไม่มีรายการสินค้า",
            )
        }
    }
}

@Composable
fun RequestDiscountItemsScreen_BarcodeDetailsContainer(
    productData: BdDetailProductUIModel,
    onBarcodeSelected: (String) -> Unit,
    onRemoveItem: (BdDetailProductUIModel) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TachyonDictionary.surface.static._default2)
                .selectable(
                    selected = productData.barcode.isNotEmpty(),
                    onClick =
                        safeClickWithDebounce {
                            onBarcodeSelected(productData.barcode)
                        },
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = TachyonDictionary.space.padding._md,
                        top = TachyonDictionary.space.padding._xs,
                        end = TachyonDictionary.space.padding._md,
                        bottom = TachyonDictionary.space.padding._xs,
                    ),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TachyonText(
                    text = productData.barcode,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TachyonText(
                        text = (productData.numberOfDiscounted ?: 0).toString(),
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._default,
                    )
                    Spacer(
                        modifier = Modifier.width(TachyonDictionary.space.padding._2xs),
                    )
                    TachyonText(
                        text = productData.unitFactor,
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                    )
                }
            }
            TachyonText(
                text = productData.productName,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_expired_date_format,
                            productData.expiredDate?.toStringDDMMYYYYFormat() ?: "N/A",
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
                IconButton(
                    onClick = safeClickWithDebounce { onRemoveItem(productData) },
                    modifier =
                        Modifier
                            .align(Alignment.CenterVertically)
                            .background(
                                shape = RoundedCornerShape((8 / LocalDensity.current.density).dp),
                                color = TachyonDictionary.surface.action.delete._default,
                            ).size((32 / LocalDensity.current.density).dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons_delete),
                        contentDescription = stringResource(R.string.text_clear),
                        tint = TachyonDictionary.content._inversed,
                        modifier =
                            Modifier
                                .width((16 / LocalDensity.current.density).dp)
                                .height((18 / LocalDensity.current.density).dp),
                    )
                }
            }
        }
    }
}

@Composable
fun RequestDiscountItemsScreen_BottomButtonContainer(viewModel: RequestDiscountItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val upsertBdState by viewModel.upsertBdState.collectAsState()
    val bdDetailModel by viewModel.bdDetailModel.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.QUESTION,
                titleText = stringResource(id = R.string.text_confirm_to_save_document),
                messageText = stringResource(id = R.string.text_confirm_edit_doc),
                negativeTextRes = R.string.text_no,
                onNegativeClick = {
                    showConfirmDialog = false
                },
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.bdDocumentSubmit()
                    showConfirmDialog = false
                },
            ),
        )
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonButtonPrimaryOutlined(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            buttonText = stringResource(id = R.string.text_cancel),
            onClick = {
                viewModel.logEventOnBackPressed()
                context
                    .startActivity(Intent(context, RequestDiscountDocsActivity::class.java))
                    .also {
                        (context as ComponentActivity).finish()
                    }
            },
        )

        TachyonButtonPrimaryFilled(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            enabled = bdDetailModel?.products?.isNotEmpty() == true,
            buttonText = stringResource(id = R.string.text_save),
            onClick = { showConfirmDialog = true },
        )
    }
}
