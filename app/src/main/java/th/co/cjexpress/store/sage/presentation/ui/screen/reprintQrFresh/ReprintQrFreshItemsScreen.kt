package th.co.cjexpress.store.sage.presentation.ui.screen.reprintQrFresh

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.reprintQrFresh.ReprintQrFreshItemDetailActivity
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemsViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemsViewModel.UIState

class ReprintQrFreshItemsScreen(
    viewModel: ReprintQrFreshItemsViewModel,
) : BaseScreen<ReprintQrFreshItemsViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        super.TopAppBarContainer(
            configs =
                TopAppBarConfigs(
                    titleTextRes = R.string.text_menu_name_reprint_qr,
                    onNavigationIconClick = {
                        viewModel.performActionToMainMenu()
                    },
                ),
        )
    }

    @Composable
    override fun BottomAppBarContainer() {
        val documentDetailLD by viewModel.documentDetailLD.collectAsState()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonOutlinedButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                style = TachyonOutlinedButtonStyle.GENERAL,
                buttonText = stringResource(id = R.string.text_back_to_main_menu),
                onClick = { viewModel.performActionToMainMenu() },
            )
            TachyonSolidButton(
                modifier = Modifier.weight(1f),
                style = TachyonSolidButtonStyle.GENERAL,
                enabled = documentDetailLD?.items?.isNotEmpty() ?: false,
                buttonText = stringResource(R.string.text_save),
                onClick = { viewModel.promptBeforeSubmit() },
            )
        }
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is ReprintQrFreshItemUIModel -> {
                viewModel.logEventSelectItem(data)
                viewModel.prepareEditingItem(data)
                context.startActivity(
                    Intent(
                        context,
                        ReprintQrFreshItemDetailActivity::class.java,
                    ),
                )
            }

            is UIState.NavToMainMenu -> {
                context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                    (context as ComponentActivity).finish()
                }
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        val documentDetailLD by viewModel.documentDetailLD.collectAsState()

        BarcodeInputContainer()
        BarcodeItemListContainer(documentDetailLD?.items ?: emptyList())
    }

    @Composable
    fun BarcodeInputContainer() {
        TachyonBarcodeInputField(
            label = stringResource(R.string.text_product_barcode),
            placeholder = stringResource(R.string.text_please_fill_or_scan),
            decodeType = DecodeType.BARCODE_CODEC,
            acceptedPrefix =
                listOf(
                    PromotionType.COMMON_BARCODE,
                    PromotionType.QR_FRESH,
                ),
            onValidateBarcode = { decoded ->
                viewModel.searchBarcode(decoded.barcode)
            },
        )
    }

    @Composable
    fun BarcodeItemListContainer(barcodeList: List<ReprintQrFreshItemUIModel>) {
        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_product_list,
                            barcodeList.size,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text = stringResource(R.string.text_qr_print_amount),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            items = barcodeList,
            itemKey = { "${it.barcode}_${it.expireDate}_${it.qrPrintCount}" },
            isScrollToTopWhenUpdatedItem = true,
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(id = R.drawable.icon_empty_document),
                    descriptionText = stringResource(id = R.string.text_title_error_not_found_order),
                ),
        ) { productData ->
            BarcodeDetailContainer(
                productData = productData,
                onItemClick = { viewModel.setBaseScreenSuccessState(it) },
            )
        }
    }

    @Composable
    fun BarcodeDetailContainer(
        productData: ReprintQrFreshItemUIModel,
        onItemClick: (ReprintQrFreshItemUIModel) -> Unit,
    ) {
        TachyonLazyColumnItem(
            onClick = { onItemClick(productData) },
        ) {
            Column {
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
                        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
                    ) {
                        TachyonText(
                            text = "${productData.qrPrintCount}",
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )

                        TachyonText(
                            text = "ดวง",
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }

                TachyonText(
                    text = productData.skuName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_reprint_qr_expired_date_format,
                            productData.expireDate,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }
}
