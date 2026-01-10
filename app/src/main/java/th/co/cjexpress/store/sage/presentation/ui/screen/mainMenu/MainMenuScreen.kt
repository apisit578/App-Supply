package th.co.cjexpress.store.sage.presentation.ui.screen.mainMenu

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceSupplierListActivity
import th.co.cjexpress.store.sage.presentation.activity.reprintQrFresh.ReprintQrFreshItemsActivity
import th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc.RequestDiscountDocsActivity
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonMenuButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.mainMenu.MainMenuViewModel
import th.co.cjexpress.store.sage.utils.UUIDUtils

class MainMenuScreen(
    viewModel: MainMenuViewModel,
) : BaseScreen<MainMenuViewModel>(viewModel) {
    @Composable
    override fun SuccessStateCallback(data: Any?) {
        with(LocalContext.current) {
            UUIDUtils.generateUUID(data as MainMenuType)
            startActivity(
                Intent(
                    this,
                    when (data) {
                        MainMenuType.REQUEST_DISCOUNT_RTC -> RequestDiscountDocsActivity::class.java
                        MainMenuType.ORDER_RECEIVE_TOTE -> OrderReceiveToteDocsActivity::class.java
                        MainMenuType.ORDER_RECEIVE -> OrderReceiveDocsActivity::class.java
                        MainMenuType.PURCHASE_INVOICE -> PurchaseInvoiceSupplierListActivity::class.java
                        MainMenuType.REPRINT_QR -> ReprintQrFreshItemsActivity::class.java
                        else -> {
                            null
                        }
                    },
                ),
            ).also {
                (this as ComponentActivity).finish()
            }
        }
    }

    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        super.TopAppBarContainer(
            TopAppBarConfigs(
                titleTextRes = R.string.text_sage,
                titleAlignment = Alignment.CenterHorizontally,
                showNavigationIcon = false,
                onNavigationIconClick = { },
            ),
        )
    }

    @Composable
    override fun ScreenContent() {
        LaunchedEffect(Unit) {
            viewModel.loadMenuItems()
        }

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
        ) {
            items(
                items = viewModel.moduleList,
            ) { module ->
                Column(
                    modifier =
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = TachyonDictionary.border._default,
                                shape = RoundedCornerShape(10.dp),
                            ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .padding(TachyonDictionary.space.padding._md),
                    ) {
                        TachyonText(
                            text = module.moduleName,
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )

                        Column(
                            modifier =
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                        ) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
                                maxItemsInEachRow = 3,
                            ) {
                                for (menu in module.listMenu) {
                                    if (!menu.isEnable) {
                                        continue
                                    }

                                    TachyonMenuButton(
                                        menuType = menu.menuType,
                                        menuName = menu.name,
                                        menuIcon = menu.icon,
                                        enabled = menu.isEnable,
                                    ) {
                                        viewModel.onMenuClick(menu.menuType)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
