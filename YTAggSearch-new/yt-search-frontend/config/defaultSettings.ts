import { Settings as LayoutSettings } from '@ant-design/pro-components';

const Settings: LayoutSettings & {
  pwa?: boolean;
  logo?: string;
} = {
  "navTheme": "light",
  "primaryColor": "#722ED1",
  "layout": "top",
  "contentWidth": "Fluid",
  "fixedHeader": true,
  "fixSiderbar": true,
  "pwa": false,
  "logo": "https://ytbaiduren-1309783343.cos.ap-nanjing.myqcloud.com/logo%20%282%29.svg",
  "headerHeight": 48,
  "splitMenus": false,
  "menuRender": false
}

export default Settings;
