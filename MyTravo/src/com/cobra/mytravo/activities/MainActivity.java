        	case R.id.main_register:
        		intent.setClass(MainActivity.this, RegisterActivity.class);
        		break;
        	case R.id.show_travels:
        		Intent intent1 = new Intent();
        		intent1.setClass(MainActivity.this, TravelActivity.class);
        		startActivity(intent1);
        		break;
        	case R.id.add_travel:
        		Intent intent2 = new Intent();
        		intent2.setClass(MainActivity.this, AddTravelActivity.class);
        		startActivity(intent2);
        		break;
