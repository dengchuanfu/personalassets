import { definePlugin } from "@halo-dev/ui-shared";
import "uno.css";
import { markRaw } from "vue";
import CarbonFinancialAssets from "~icons/carbon/financial-assets";

export default definePlugin({
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/personalassets",
        name: "PluginPersonalAssets",
        component: () => import("@/views/EquipmentList.vue"),
        meta: {
          title: "资产",
          searchable: true,
          icon: markRaw(CarbonFinancialAssets),
          permissions: ["plugin:personalassets:view"],
          menu: {
            name: "资产",
            group: "content",
            icon: markRaw(CarbonFinancialAssets),
            priority: 40,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/assets",
        name: "Assets",
        redirect: { name: "PluginPersonalAssets" },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/equipments",
        name: "Equipments",
        redirect: { name: "PluginPersonalAssets" },
      },
    },
  ],
});
